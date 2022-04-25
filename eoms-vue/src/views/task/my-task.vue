<template>
    <div class="app-container">
        <div align="center">
            <el-form :model="dataForm" ref="dataForm" :inline="true" label-width="80px">
                <el-form-item label="发布日期" prop="publishTime" label-width="80px">
                    <el-date-picker
                            v-model="dataForm.publishTime"
                            type="date"
                            placeholder="发布日期"
                            class="input"
                            size="small"
                    ></el-date-picker>
                </el-form-item>
                <el-form-item label="任务状态" prop="taskStatus">
                    <el-select
                            v-model="dataForm.taskStatus"
                            class="input"
                            placeholder="任务状态"
                            size="small"
                            clearable="clearable"
                    >
                        <el-option label="新任务" value="1"></el-option>
                        <el-option label="已取消" value="2"></el-option>
                        <el-option label="进行中" value="3"></el-option>
                        <el-option label="已完成" value="4"></el-option>
                        <el-option label="已评分" value="5"></el-option>
                        <el-option label="已过期" value="0"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item style="margin-left: 20px">
                    <el-button type="primary" icon="el-icon-search" size="mini" @click="searchHandle">搜索</el-button>
                    <el-button icon="el-icon-refresh" size="mini" @click="resetForm">重置</el-button>
                </el-form-item>
            </el-form>
        </div>

        <el-table v-loading="dataListLoading" :data="dataList" @selection-change="selectionChangeHandle" :header-cell-style="{background:'#eef1f6',color:'#606266'}">
            <el-table-column type="selection" width="60" align="center" />
            <el-table-column type="index" header-align="center" align="center" min-width="50px" label="序号">
                <template #default="scope">
                    <span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column label="任务主题" min-width="120px" align="center" prop="theme" :show-overflow-tooltip="true"/>
            <el-table-column label="发布人" align="center" prop="publisherName" min-width="100px"></el-table-column>
            <el-table-column label="发布时间" align="center" prop="publishTime" min-width="120px"></el-table-column>
            <el-table-column label="开始时间" align="center" prop="startTime" min-width="120px"></el-table-column>
            <el-table-column label="截止时间" align="center" prop="endTime" min-width="120px"></el-table-column>
            <el-table-column label="状态" align="center" prop="status" min-width="80px">
                <template #default="scope">
                    <el-tag type="warning" v-if="scope.row.taskStatus === '新任务'">{{scope.row.taskStatus}}</el-tag>
                    <el-tag type="info" v-if="scope.row.taskStatus === '已取消'">{{scope.row.taskStatus}}</el-tag>
                    <el-tag type="primary" v-if="scope.row.taskStatus === '进行中'">{{scope.row.taskStatus}}</el-tag>
                    <el-tag type="success" v-if="scope.row.taskStatus === '已完成'">{{scope.row.taskStatus}}</el-tag>
                    <el-tag type="success" v-if="scope.row.taskStatus === '已评分'">{{scope.row.taskStatus}}</el-tag>
                    <el-tag type="danger" v-if="scope.row.taskStatus === '已过期'">{{scope.row.taskStatus}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column header-align="center" align="center" label="操作" min-width="120">
                <template #default="scope">
                    <el-button
                            icon="el-icon-search"
                            type="text"
                            size="small"
                            @click="searchTaskInfo(scope.row.id, false)"
                    >
                        查看
                    </el-button>
                    <el-button
                            icon="el-icon-edit"
                            type="text"
                            size="small"
                            :disabled="scope.row.taskStatus === '已取消' || scope.row.taskStatus === '已过期' ||
                                        scope.row.taskStatus === '已完成' || scope.row.taskStatus === '已评分'"
                            @click="searchTaskInfo(scope.row.id, true)"
                    >
                        完成任务
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
        <task-info v-if="infoVisible" ref="info" @refreshDataList="loadDataList"></task-info>
        <task-rate v-if="rateVisible" ref="rate" @refreshDataList="loadDataList"></task-rate>
    </div>
</template>

<script>
    import taskInfo from "./task-info.vue";
    import taskRate from "./task-rate.vue";
    export default {
        components: {
            taskInfo,
            taskRate
        },
        data() {
            return {
                dataForm: {
                    publishTime: null,
                    taskStatus: null
                },
                dataList: [],
                pageIndex: 1,
                pageSize: 10,
                totalCount: 0,
                dataListLoading: false,
                dataListSelections: [],
                // 非单个禁用
                single: true,
                // 非多个禁用
                multiple: true,
                infoVisible: false,
                rateVisible: false
            };
        },
        created: function() {
            this.loadDataList()
        },
        methods: {
            // 加载数据
            loadDataList: function() {
                let that = this
                that.dataListLoading = true
                let data = {
                    publishTime: that.dataForm.publishTime,
                    taskStatus: that.dataForm.taskStatus,
                    page: that.pageIndex,
                    length: that.pageSize
                }
                if (that.dataForm.publishTime != null && that.dataForm.publishTime !== '') {
                    data.publishTime = dayjs(that.dataForm.publishTime).format('YYYY-MM-DD')
                }
                that.$http('/task/searchMyTaskList', 'POST', data, true, function (resp) {
                    let page = resp.page
                    for (let one of page.list) {
                        if (one.taskStatus === 0) {
                            one.taskStatus = '已过期'
                        } else if (one.taskStatus === 1) {
                            one.taskStatus = '新任务'
                        } else if (one.taskStatus === 2) {
                            one.taskStatus = '已取消'
                        } else if (one.taskStatus === 3) {
                            one.taskStatus = '进行中'
                        } else if (one.taskStatus === 4) {
                            one.taskStatus = '已完成'
                        } else if (one.taskStatus === 5) {
                            one.taskStatus = '已评分'
                        }
                    }
                    that.dataList = page.list
                    that.totalCount = page.totalCount
                    that.dataListLoading = false
                })
            },
            // 条件查询
            searchHandle: function() {
                let that = this
                //先执行表单验证
                that.$refs['dataForm'].validate(valid => {
                    if (valid) {
                        //清理页面上的表单验证结果
                        that.$refs['dataForm'].clearValidate();
                        //如果当前页面不是第一页，则跳转到第一页显示查询的结果
                        if (that.pageIndex !== 1) {
                            that.pageIndex = 1;
                        }
                        that.loadDataList()
                    } else {
                        return false;
                    }
                });
            },
            // 复选框
            selectionChangeHandle: function(val) {
                this.dataListSelections = val
                this.single = val.length !== 1
                this.multiple = !val.length
            },
            // 每页多少数据
            sizeChangeHandle: function (val) {
                this.pageSize = val
                this.pageIndex = 1
                this.loadDateList()
            },
            // 页数变化
            currentChangeHandle: function (val) {
                this.pageIndex = val
                this.loadDateList()
            },
            // 重置表单
            resetForm: function () {
                this.dataForm = {}
                this.pageIndex = 1
                this.loadDataList()
            },
            // 查看任务详情
            searchTaskInfo: function (id, isUpdate) {
                this.infoVisible = true
                this.$nextTick(() => {
                    this.$refs.info.init(id, isUpdate)
                })
            }
        }
    }
</script>

<style scoped>

</style>