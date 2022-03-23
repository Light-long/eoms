<template>
    <div class="app-container">
        <el-form :model="dataForm" ref="dataForm" :inline="true" label-width="68px">
            <el-form-item label="公告标题" prop="title">
                <el-input
                        v-model="dataForm.title"
                        placeholder="请输入公告标题"
                        clearable
                        size="small"
                        @keyup.enter.native="searchHandle"
                />
            </el-form-item>
            <el-form-item label="更新时间" prop="updateTime" >
                <el-date-picker
                        v-model="dataForm.updateTime"
                        style="width: 200px;"
                        type="date"
                        size="small"
                        placeholder="请选择日期"
                        clearable="clearable"
                ></el-date-picker>
            </el-form-item>
            <el-form-item label="公告状态" prop="status">
                <el-select v-model="dataForm.status" placeholder="公告状态" clearable size="small">
                    <el-option label="关闭" value="0"></el-option>
                    <el-option label="正常" value="1"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" icon="el-icon-search" size="mini" @click="searchHandle">搜索</el-button>
                <el-button icon="el-icon-refresh" size="mini" @click="reset">重置</el-button>
            </el-form-item>
        </el-form>

        <el-row :gutter="15" class="mb8" style="margin-bottom: 10px">
            <el-col :span="1.5">
                <el-button
                        type="primary"
                        plain
                        icon="el-icon-plus"
                        size="mini"
                        @click="addHandle"
                >新增</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button
                        type="success"
                        plain
                        icon="el-icon-edit"
                        size="mini"
                        :disabled="single"
                        @click="updateHandle(dataListSelections.map(item => item.id)[0])"
                >修改</el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button
                        type="danger"
                        plain
                        icon="el-icon-delete"
                        size="mini"
                        :disabled="multiple"
                        @click="deleteHandle()"
                >删除</el-button>
            </el-col>
        </el-row>

        <el-table v-loading="dataListLoading" :data="dataList" @selection-change="selectionChangeHandle" :header-cell-style="{background:'#eef1f6',color:'#606266'}">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column type="index" header-align="center" align="center" min-width="50px" label="序号">
                <template #default="scope">
                    <span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column label="公告标题" min-width="200px" align="center" prop="title" :show-overflow-tooltip="true"/>
            <el-table-column label="状态" align="center" prop="status" min-width="100px">
                <template #default="scope">
                    <el-tag class="ml-2" type="danger" v-if="scope.row.status === '关闭'">{{scope.row.status}}</el-tag>
                    <el-tag class="ml-2" type="success" v-if="scope.row.status === '正常'">{{scope.row.status}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center" prop="createTime" min-width="150px">
            </el-table-column>
            <el-table-column label="更新时间" align="center" prop="updateTime" min-width="150px">
            </el-table-column>
            <el-table-column label="置顶状态" align="center" prop="isTopping" min-width="100px">
                <template #default="scope">
                    <el-tag type="info" v-if="scope.row.isTopping === '不置顶'">{{scope.row.isTopping}}</el-tag>
                    <el-tag class="ml-2" type="primary" v-if="scope.row.isTopping === '置顶'">{{scope.row.isTopping}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" align="center" min-width="150px" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-button
                            size="medium"
                            type="text"
                            icon="el-icon-edit"
                            @click="updateHandle(scope.row.id)"
                    >修改</el-button>
                    <el-button
                            size="medium"
                            type="text"
                            icon="el-icon-delete"
                            @click="deleteHandle(scope.row.id)"
                    >删除</el-button>
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
        <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="loadDataList"></add-or-update>
    </div>
</template>

<script>
    import AddOrUpdate from './notice-add-update.vue';
    import dayjs from 'dayjs';
    export default {
        name: "notice",
        components: {
            AddOrUpdate
        },
        data: function () {
            return {
                pageIndex: 1,
                pageSize: 10,
                totalCount: 0,
                dataListLoading: false,
                dataForm: {
                    title: null,
                    updateTime: null,
                    status: null
                },
                dataList: [],
                // 非单个禁用
                single: true,
                // 非多个禁用
                multiple: true,
                addOrUpdateVisible: false,
                dataListSelections: [],
            }
        },
        created: function() {
            this.loadDataList()
        },
        methods: {
            loadDataList: function() {
                let that = this
                that.dataListLoading = true
                let data = {
                    title: that.dataForm.title,
                    updateTime: that.dataForm.updateTime,
                    status: that.dataForm.status,
                    page: that.pageIndex,
                    length: that.pageSize
                }
                if (that.dataForm.updateTime != null && that.dataForm.updateTime !== '') {
                    data.updateTime = dayjs(that.dataForm.updateTime).format("YYYY-MM-DD")
                }
                that.$http('/notice/searchNoticeByPage', 'POST', data, true, function (resp) {
                    let page = resp.page
                    for (let one of page.list) {
                        if (one.status === 0) {
                            one.status = '关闭';
                        } else if (one.status === 1) {
                            one.status = '正常';
                        }
                        if (one.isTopping === 0) {
                            one.isTopping = '不置顶'
                        } else if (one.isTopping === 1) {
                            one.isTopping = '置顶'
                        }
                    }
                    that.dataList = page.list
                    that.totalCount = page.totalCount
                    that.dataListLoading = false
                })
            },
            // 条件查询
            searchHandle: function() {
                this.$refs['dataForm'].validate(valid => {
                    if (valid) {
                        this.$refs['dataForm'].clearValidate()
                        if (this.dataForm.title === '') {
                            this.dataForm.title = null
                        }
                        if (this.pageIndex !== 1) {
                            this.pageIndex = 1
                        }
                        this.loadDataList()
                    } else {
                        return false
                    }
                })
            },
            // 重置
            reset: function() {
                this.dataForm = {}
                this.loadDataList()
            },
            // 分页
            sizeChangeHandle: function(val) {
                this.pageSize = val;
                this.pageIndex = 1;
                this.loadDataList();
            },
            currentChangeHandle: function(val) {
                this.pageIndex = val;
                this.loadDataList();
            },
            // 多选框选中数据
            selectionChangeHandle: function (val) {
                this.dataListSelections = val
                this.single = val.length !== 1
                this.multiple = !val.length
            },
            // 添加
            addHandle: function () {
                this.addOrUpdateVisible = true
                this.$nextTick(() => {
                    this.$refs.addOrUpdate.init()
                })
            },
            updateHandle: function (id) {
                this.addOrUpdateVisible = true
                this.$nextTick(() => {
                    this.$refs.addOrUpdate.init(id)
                })
            },
            deleteHandle: function(id) {
                let that = this
                let ids = id ? [id] : this.dataListSelections.map(item =>item.id)
                that.$confirm(`确定要删除选中的记录？`, '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '点错了',
                    type: 'warning'
                }).then(() => {
                    that.$http('/notice/deleteNoticeByIds', "POST", {ids: ids}, true, function (resp) {
                        if (resp.rows > 0) {
                            that.$message({
                                message: '操作成功',
                                type: 'success',
                                duration: 1200
                            });
                            that.loadDataList();
                        } else {
                            that.$message({
                                message: '未能删除记录',
                                type: 'warning',
                                duration: 1200
                            });
                        }
                    })
                })
            }
        }
    }
</script>

<style lang="less" scoped>

</style>