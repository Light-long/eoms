<template>
    <div>
        <div align="center">
            <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm" class="form">
                <el-form-item prop="date">
                    <el-date-picker
                            v-model="dataForm.date"
                            type="date"
                            placeholder="选择日期"
                            class="input"
                            size="medium"
                    ></el-date-picker>
                </el-form-item>
                <el-form-item>
                    <el-button size="medium" type="primary" @click="searchHandle()">查询</el-button>
                    <el-button size="medium" type="common" @click="reset">重置</el-button>
                    <el-button size="medium" type="success" @click="addHandle()">会议申请</el-button>
                </el-form-item>
                <el-form-item class="mold">
                    <el-radio-group v-model="dataForm.mold" size="medium" @change="changeHandle">
                        <el-radio-button label="全部会议"></el-radio-button>
                        <el-radio-button label="我的会议"></el-radio-button>
                    </el-radio-group>
                </el-form-item>
            </el-form>
        </div>
        <el-table
            :data="dataList"
            border
            v-loading="dataListLoading"
            @selection-change="selectionChangeHandle"
            cell-style="padding: 4px 0"
            style="width: 100%;"
            size="medium"
        >
            <el-table-column width="40px" prop="desc" header-align="center" align="center" type="expand">
                <template #default="scope">
                    会议内容：{{ scope.row.desc }}
                </template>
            </el-table-column>
            <el-table-column type="index" header-align="center" align="center" width="80" label="序号">
                <template #default="scope">
                    <span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="title" header-align="center" align="center" label="会议主题" min-width="200" />
            <el-table-column prop="creatorName" header-align="center" align="center" min-width="100"  label="创建者" />
            <el-table-column prop="date" header-align="center" align="center" min-width="150"  label="日期" />
            <el-table-column header-align="center" align="center" min-width="150"  label="时间">
                <template #default="scope">
                    <span>{{ scope.row.start }} ~ {{ scope.row.end }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="num" header-align="center" align="center" min-width="100"  label="人数" />
            <el-table-column prop="status" header-align="center" align="center" min-width="100" label="状态">
                <template #default="scope">
                    <span v-if="scope.row.status === '待审批'" style="color: orange;">待审批</span>
                    <span v-if="scope.row.status === '未开始'" style="color: #17B3A3;">未开始</span>
                    <span v-if="scope.row.status === '进行中'" style="color: #f56c6c;">进行中</span>
                    <span v-if="scope.row.status === '已结束'" style="color: grey;">已结束</span>
                </template>
            </el-table-column>
            <el-table-column header-align="center" align="center" width="150" label="操作">
                <template #default="scope">
                    <el-button
                        type="text"
                        size="medium"
                        :disabled="!scope.row.canEnterMeeting"
                        @click="enterHandle(scope.row.id, scope.row.uuid)"
                    >
                        进入
                    </el-button>
                    <el-button
                        type="text"
                        size="medium"
                        :disabled="
                            !(
                                (scope.row.status === '待审批' || scope.row.status === '未开始') &&
                                scope.row.isCreator === 'true'
                            )
                        "
                        @click="deleteHandle(scope.row)"
                    >
                        删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
            :current-page="pageIndex"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            :total="totalCount"
            layout="total, sizes, prev, pager, next, jumper"
        ></el-pagination>
        <add v-if="addVisible" ref="add" @refresh="refresh"></add>
    </div>
</template>

<script>
import Add from './online_meeting-add.vue';
import dayjs from 'dayjs';

export default {
    components: { Add },
    data: function() {
        return {
            dataForm: {
                date: null,
                mold: '全部会议'
            },
            dataList: [],
            pageIndex: 1,
            pageSize: 10,
            totalCount: 0,
            dataListLoading: false,
            addVisible: false,
            dataRule: {}
        };
    },
    created: function() {
        this.loadDataList()
    },
    methods: {
        loadDataList: function () {
            let that = this
            that.dataListLoading = true
            let data = {
                mold: that.dataForm.mold,
                page: that.pageIndex,
                length: that.pageSize
            }
            if (that.dataForm.date != null && that.dataForm.date !== '') {
                data.date = dayjs(that.dataForm.date).format("YYYY-MM-DD")
            }
            that.$http("/meeting/searchOnlineMeetingByPage", "POST", data, true, function (resp) {
                let page = resp.page
                for (let one of page.list) {
                    if (one.status === 1) {
                        one.status = '待审批';
                    } else if (one.status === 3) {
                        one.status = '未开始';
                    } else if (one.status === 4) {
                        one.status = '进行中';
                    } else if (one.status === 5) {
                        one.status = '已结束';
                    }
                    //计算会议是否可以进入
                    //1.开会前15分钟可以进入会议室
                    //2.会议状态必须是未开始或者进行中
                    let minute = dayjs(new Date()).diff(dayjs(`${one.date} ${one.start}`), 'minute')
                    one.canEnterMeeting = one.mine === 'true' && (minute >= -12 && minute <= 0 && one.status === '未开始' || one.status === '进行中');
                    that.dataList = page.list
                    that.totalCount = page.totalCount
                    that.dataListLoading = false
                }
            })
        },
        sizeChangeHandle: function (val) {
            this.pageIndex = 1
            this.pageSize = val
            this.loadDataList()
        },
        currentChangeHandle: function (val) {
            this.pageIndex = val
            this.loadDataList()
        },
        searchHandle: function () {
            this.$refs['dataForm'].validate(valid => {
                if (valid) {
                    this.$refs['dataForm'].clearValidate()
                    if (this.dataForm.date === '') {
                        this.dataForm.date = null;
                    }
                    if (this.pageIndex !== 1) {
                        this.pageIndex = 1;
                    }
                    this.loadDataList()
                } else {
                    return false
                }
            })
        },
        changeHandle: function () {
            this.searchHandle()
        },
        reset: function () {
            this.dataForm = []
            this.dataForm.mold = '全部会议'
            this.searchHandle()
        },
        refresh: function () {
            this.$refs['dataForm'].resetFields()
            this.loadDataList()
        },
        addHandle: function() {
            this.addVisible = true
            this.$nextTick(() => {
                this.$refs.add.init();
            });
        },
        deleteHandle: function(json) {
            let that = this;
            that.$confirm('是否删除该会议?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                let data = {
                    id: json.id,
                    uuid: json.uuid,
                    instanceId: json.instanceId,
                    reason: "删除线上会议"
                }
                that.$http("/meeting/deleteMeetingApplication", "POST", data, true, function (resp) {
                    if (resp.rows === 1) {
                        that.$message({
                            message: '删除成功',
                            type: 'success',
                            duration: 1200
                        });
                        that.searchHandle();
                    } else {
                        that.$message({
                            message: '删除失败',
                            type: 'error',
                            duration: 1200
                        });
                    }
                })
            })
        },
        enterHandle: function (meetingId, uuid) {
            this.$router.push({name: 'MeetingVideo', params: {meetingId: meetingId, uuid: uuid}})
        }
    }
};
</script>

<style lang="less" scoped="scoped">
    @import url('online_meeting.less');
</style>
