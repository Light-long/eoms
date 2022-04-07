<template>
    <div>
        <!--条件表单-->
        <div align="center">
            <el-form :inline="true" :model="dataForm" ref="dataForm">
                <el-form-item prop="date" label="待办日期">
                    <el-date-picker
                            v-model="dataForm.date"
                            style="width: 200px;"
                            type="date"
                            size="small"
                            placeholder="请选择日期"
                            clearable="clearable"
                    ></el-date-picker>
                </el-form-item>
                <el-form-item prop="status" label="状态">
                    <el-select
                        v-model="dataForm.status"
                        class="input"
                        placeholder="完成状态"
                        size="small"
                        clearable="clearable"
                    >
                        <el-option label="待完成" value="1"></el-option>
                        <el-option label="已过期" value="2"></el-option>
                        <el-option label="已完成" value="3"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="el-icon-search" size="mini" @click="searchHandle">搜索</el-button>
                    <el-button icon="el-icon-refresh" size="mini" @click="reset">重置</el-button>
                </el-form-item>
            </el-form>
        </div>

        <!--按钮-->
        <el-row :gutter="15" class="mb8" style="margin-bottom: 10px">
            <el-col :span="1.5">
                <el-button
                        type="success"
                        plain
                        icon="el-icon-plus"
                        size="mini"
                        @click="addHandle()"
                >添加待办</el-button>
            </el-col>
        </el-row>

        <!--表格-->
        <div v-show="mold === 'table'">
            <el-table v-loading="dataListLoading" :data="dataList" :header-cell-style="{background:'#eef1f6',color:'#606266'}">
                <el-table-column type="index" header-align="center" align="center" width="60" label="序号">
                    <template #default="scope">
                        <span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="title" header-align="center" align="center" label="待办事项名" min-width="120" ></el-table-column>
                <el-table-column prop="desc" header-align="center" align="center" label="待办事项详情" min-width="250"></el-table-column>
                <el-table-column prop="start" header-align="center" align="center" label="起始时间" min-width="150"></el-table-column>
                <el-table-column prop="end" header-align="center" align="center" label="截止时间" min-width="150"></el-table-column>
                <el-table-column prop="priority" header-align="center" align="center" label="优先级" min-width="100">
                    <template #default="scope">
                        <span v-if="scope.row.priority === '低级'" style="color: #17B3A3;">{{ scope.row.priority }}</span>
                        <span v-if="scope.row.priority === '中级'" style="color: orange;">{{ scope.row.priority }}</span>
                        <span v-if="scope.row.priority === '高级'" style="color: #f56c6c;">{{ scope.row.priority }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="status" header-align="center" align="center" label="完成状态" min-width="100">
                    <template #default="scope">
                        <span v-if="scope.row.status === '待完成'" style="color: orange;">{{ scope.row.status }}</span>
                        <span v-if="scope.row.status === '已过期'" style="color: grey">{{ scope.row.status }}</span>
                        <span v-if="scope.row.status === '已完成'" style="color: #17B3A3;">{{ scope.row.status }}</span>
                    </template>
                </el-table-column>
                <el-table-column header-align="center" align="center" width="150" label="操作" min-width="120">
                    <template #default="scope">
                        <el-button
                                icon="el-icon-circle-check"
                                type="text"
                                size="medium"
                                :disabled=" !(new Date().getTime() > new Date(scope.row.start).getTime() && new Date().getTime() < new Date(scope.row.end).getTime())
                                            || (scope.row.status === '已完成' || scope.row.status === '已过期')"
                                @click="finishHandle(scope.row.id)"
                        >
                            完成
                        </el-button>
                        <el-button
                                icon="el-icon-delete"
                                type="text"
                                size="medium"
                                :disabled="scope.row.status === '已完成' || scope.row.status === '已过期'"
                                @click="deleteHandle(scope.row.id)"
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
        </div>
        <div v-show="mold === 'timeline'">
            <div v-if="dataList.length === 0">
                <el-card shadow="none" class="box-card">
                    <div align="center">暂无数据</div>
                </el-card>
            </div>
            <el-timeline v-if="dataList.length !== 0">
                <el-timeline-item
                        v-for="(data, index) in dataList"
                        :key="index"
                        :timestamp= "data.start"
                        :type="data.status === '待完成' ? 'warning' : data.status === '已过期'
                                                ? 'info' : 'success'"
                        placement="top"
                        size="large"
                >
                    <el-card>
                        <h2>{{ data.title }}</h2>
                        <h3>{{ data.desc }}</h3>
                        <h4>截止时间：</h4> {{data.end}} &nbsp;
                        <h4>优先级：</h4> {{data.priority}}
                    </el-card>
                </el-timeline-item>
            </el-timeline>
        </div>
        <todo-add v-if="addVisible" ref="add" @refreshDataList="loadDataList"></todo-add>
    </div>
</template>

<script>
    import dayjs from "dayjs";
    import TodoAdd from './todo-add.vue'
    export default {
        components: {
            TodoAdd
        },
        data: function () {
            return {
                pageIndex: 1,
                pageSize: 10,
                totalCount: 0,
                dataListLoading: false,
                dataForm: {
                    status: null,
                    date: null
                },
                dataList: [],
                addVisible: false,
                mold: 'table',
                // 非单个禁用
                single: true,
                // 非多个禁用
                multiple: true
            }
        },
        created: function() {
            this.loadDataList()
        },
        methods: {
            loadDataList: function () {
                let that = this
                that.dataListLoading = true
                let data = {
                    date: that.dataForm.date,
                    status: that.dataForm.status,
                    page: that.pageIndex,
                    length: that.pageSize
                }
                if (that.dataForm.date != null && that.dataForm.date !== '') {
                    data.date = dayjs(that.dataForm.date).format("YYYY-MM-DD")
                } else {
                    // 默认只看今天的待办
                    data.date = dayjs(new Date()).format("YYYY-MM-DD")
                }
                that.$http('/todo/searchTodoList', 'POST', data, true, function (resp) {
                    let todoList = resp.todoList
                    for (let one of todoList.list) {
                        if (one.priority === 1) {
                            one.priority = '低级'
                        } else if (one.priority === 2) {
                            one.priority = '中级';
                        } else if (one.priority === 3) {
                            one.priority = '高级';
                        }
                        if (one.status === 1) {
                            one.status = '待完成';
                        } else if (one.status === 2) {
                            one.status = '已过期';
                        } else if (one.status === 3) {
                            one.status = '已完成';
                        }
                    }
                    that.dataList = todoList.list
                    that.totalCount = todoList.totalCount
                    that.dataListLoading = false
                })
            },
            sizeChangeHandle: function(val) {
                this.pageSize = val;
                this.pageIndex = 1;
                this.loadDataList();
            },
            currentChangeHandle: function(val) {
                this.pageIndex = val;
                this.loadDataList();
            },
            searchHandle: function() {
                let that = this
                if (that.dataForm.date == null || that.dataForm.date === '') {
                    that.$refs['dataForm'].validate(valid => {
                        if (valid) {
                            that.$refs['dataForm'].clearValidate()
                            if (that.pageIndex !== 1) {
                                that.pageIndex = 1
                            }
                            that.loadDataList()
                            that.mold = 'table'
                        } else {
                            return false
                        }
                    })
                } else {
                    that.$refs['dataForm'].validate(valid => {
                        if (valid) {
                            that.$refs['dataForm'].clearValidate()
                            // 查询出这个日期的全部待办事项，以时间线表示
                            let data = {
                                date: dayjs(that.dataForm.date).format("YYYY-MM-DD"),
                                status: that.dataForm.status
                            }
                            that.$http('/todo/searchTodoListByDate', 'POST', data, true, function (resp) {
                                let todoList = resp.list
                                for (let one of todoList) {
                                    if (one.priority === 1) {
                                        one.priority = '低级'
                                    } else if (one.priority === 2) {
                                        one.priority = '中级';
                                    } else if (one.priority === 3) {
                                        one.priority = '高级';
                                    }
                                    if (one.status === 1) {
                                        one.status = '待完成';
                                    } else if (one.status === 2) {
                                        one.status = '已过期';
                                    } else if (one.status === 3) {
                                        one.status = '已完成';
                                    }
                                }
                                that.dataList = todoList
                                that.mold = 'timeline'
                            })
                        } else {
                            return false
                        }
                    })
                }
            },
            reset: function () {
                this.dataForm = {}
                this.mold = 'table'
                this.loadDataList()
            },
            finishHandle: function (id) {
                let that = this
                that.$confirm('你是否完成了此待办事项？', '提示', {
                    confirmButtonText: '完成了',
                    cancelButtonText: '点错了',
                    type: 'warning',
                    // 去除关闭图标
                    distinguishCancelAndClose: true,
                    callback: function(action) {
                        if (action === 'confirm') {
                            that.finish(id)
                        }
                    }
                })
            },
            finish: function (id) {
                let that = this
                that.dataListLoading = true
                let data = {
                    id: id,
                    status: 3
                }
                that.$http('/todo/finishTask', 'POST', data, true, function (resp) {
                    if (resp.rows === 1) {
                        that.pageIndex = 1
                        that.loadDataList()
                        that.$message({
                            message: '完成待办',
                            type: 'success',
                            duration: 1200
                        });
                    } else {
                        that.$message({
                            message: '后端出现错误',
                            type: 'error',
                            duration: 1000
                        });
                    }
                })
            },
            addHandle: function () {
                this.addVisible = true
                this.$nextTick(() => {
                    this.$refs.add.init()
                })
            },
            deleteHandle: function (id) {
                let that = this
                that.$confirm(`确定要删除此项待办？`, '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '点错了',
                    type: 'warning'
                }).then(() => {
                    that.$http('/todo/deleteTodoList', 'POST', {id: id}, true, function (resp) {
                        if (resp.rows === 1) {
                            that.pageIndex = 1
                            that.loadDataList()
                            that.$message({
                                message: '删除待办成功',
                                type: 'success',
                                duration: 1200
                            });
                        } else {
                            that.$message({
                                message: '删除失败',
                                type: 'error',
                                duration: 1000
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