<template>
    <div class="app-container" v-if="isAuth(['ROOT', 'ROLE:SELECT'])">
        <!--查询表单-->
        <div align="center">
            <el-form :model="dataForm" ref="dataForm" :inline="true" :rules="dataRule" label-width="85px">
                <el-form-item label="角色名称"  prop="roleName">
                    <el-input
                            v-model="dataForm.roleName"
                            placeholder="角色名称"
                            size="small"
                            class="input"
                            clearable="clearable"
                            @keyup.enter.native="searchHandle"
                    />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="el-icon-search" size="mini" @click="searchHandle">搜索</el-button>
                    <el-button icon="el-icon-refresh" size="mini" @click="resetForm">重置</el-button>
                </el-form-item>
            </el-form>
        </div>
        <!--按钮-->
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
            <el-col :span="1.5">
                <el-button
                        type="primary"
                        plain
                        icon="el-icon-download"
                        size="mini"
                        @click="exportDataAll()"
                >导出全部</el-button>
            </el-col>
        </el-row>

        <el-table v-loading="dataListLoading" :data="dataList" @selection-change="selectionChangeHandle" :header-cell-style="{background:'#eef1f6',color:'#606266'}">
        <el-table-column
                type="selection"
                :selectable="selectable"
                header-align="center"
                align="center"
                width="50"
            />
            <el-table-column type="index" header-align="center" align="center" width="80" label="序号">
                <template #default="scope">
                    <span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="roleName" header-align="center" align="center" label="角色名称" min-width="180"/>
            <el-table-column header-align="center" align="center" label="权限数量" min-width="140">
                <template #default="scope">
                    <span>{{ scope.row.permissions }}个</span>
                </template>
            </el-table-column>
            <el-table-column prop="users" header-align="center" align="center" label="关联用户" min-width="120">
                <template #default="scope">
                    <span>{{ scope.row.users }}人</span>
                </template>
            </el-table-column>
            <el-table-column prop="desc" header-align="center" align="center" label="备注" min-width="250" />
            <el-table-column prop="systemic" header-align="center" align="center" label="内置角色" min-width="100">
                <template #default="scope">
<!--                    <span>{{ scope.row.systemic ? '是' : '否' }}</span>-->
                    <el-tag type="primary" v-if="scope.row.systemic === true">是</el-tag>
                    <el-tag type="info" v-if="scope.row.systemic === false">否</el-tag>
                </template>
            </el-table-column>
            <el-table-column header-align="center" align="center" width="150" label="操作">
                <template #default="scope">
                    <el-button
                        type="text"
                        size="medium"
                        icon="el-icon-edit"
                        :disabled="!isAuth(['ROOT', 'ROLE:UPDATE']) || scope.row.id === 0"
                        @click="updateHandle(scope.row.id, scope.row.systemic)"
                    >
                        修改
                    </el-button>
                    <el-button
                        type="text"
                        size="medium"
                        icon="el-icon-delete"
                        :disabled="!isAuth(['ROOT', 'ROLE:DELETE']) || scope.row.systemic || scope.row.users > 0"
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
        <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="loadDataList"></add-or-update>
    </div>
</template>

<script>
import AddOrUpdate from './role-add-or-update.vue';
export default {
    components: {
        AddOrUpdate
    },
    data: function() {
        return {
            dataForm: {
                roleName: null
            },
            dataList: [],
            pageIndex: 1,
            pageSize: 10,
            totalCount: 0,
            dataListLoading: false,
            dataListSelections: [],
            addOrUpdateVisible: false,
            // 非单个禁用
            single: true,
            // 非多个禁用
            multiple: true,
            dataRule: {
                roleName: [{ required: false, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{1,10}$', message: '角色格式错误' }]
            }
        };
    },
    created: function() {
        this.loadDataList()
    },
    methods: {
        selectionChangeHandle: function(val) {
            this.dataListSelections = val
            this.single = val.length !== 1
            this.multiple = !val.length
        },
        selectable: function(row) {
            return !(row.systemic || row.users > 0);

        },
        sizeChangeHandle: function(val) {
            this.pageSize = val
            this.pageIndex = 1
            this.loadDataList()
        },
        currentChangeHandle: function(val) {
            this.pageIndex = val
            this.loadDataList()
        },
        loadDataList: function () {
            let that = this
            that.dataListLoading = true
            let data = {
                roleName: that.dataForm.roleName,
                page: that.pageIndex,
                length: that.pageSize
            }
            that.$http("/role/searchRolesByPage", "POST", data, true, function (resp) {
                let page = resp.rolePage
                that.dataList = page.list
                that.totalCount = page.totalCount
                that.dataListLoading = false
            })
        },
        searchHandle: function () {
            let that = this
            that.$refs['dataForm'].validate(valid => {
                if (valid) {
                    this.$refs['dataForm'].clearValidate()
                    if (that.dataForm.roleName === '') {
                        that.dataForm.roleName = null
                    }
                    if (that.pageIndex > 1) {
                        that.pageIndex = 1
                    }
                    that.loadDataList()
                } else {
                    return false
                }
            })
        },
        resetForm: function () {
            this.dataForm = {}
            this.pageIndex = 1
            this.loadDataList()
        },
        addHandle: function () {
            this.addOrUpdateVisible = true
            this.$nextTick(() => {
                this.$refs.addOrUpdate.init();
            });
        },
        updateHandle: function(id, systemic) {
            this.addOrUpdateVisible = true;
            this.$nextTick(() => {
                this.$refs.addOrUpdate.init(id, systemic)
            });
        },
        deleteHandle: function (id) {
            let that = this
            let ids = id ? [id] : that.dataListSelections.map(item => item.id)
            if (ids.length == 0) {
                that.$message({
                    message: '没有选中记录',
                    type: 'warning',
                    duration: 1200
                });
            } else {
                that.$confirm(`确定要删除选中的记录？`, '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then( () => {
                    that.$http("/role/deleteRoleByIds", "POST", {ids: ids}, true, function (resp) {
                        if (resp.rows > 0) {
                            that.$message({
                                message: '操作成功',
                                type: 'success',
                                duration: 1200
                            });
                            that.loadDataList()
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
        },
        exportDataAll: function () {
            //处理封装roleList对象
            let roleList = []
            for (let i = 0; i < this.dataList.length; i++) {
                let role = this.dataList[i]
                if (role.systemic === true) {
                    role.systemic = '是'
                } else if (role.systemic === false) {
                    role.systemic = '否'
                }
                roleList[i] = role
            }
            let data = {
                title: "角色数据表",
                data: JSON.stringify(roleList)
            }
            console.log(data)
            let that = this
            that.$http('/excel/exportRoleExcel', 'POST', data, true, function (resp) {
                if (resp.code === 200) {
                    that.$message({
                        type: 'success',
                        duration: 1200,
                        message: '成功导出到：' + resp.path
                    });
                } else {
                    that.$message({
                        type: 'error',
                        duration: 1200,
                        message: '导出失败:' + "请检查文件是不是已经存在或打开"
                    });
                }
            })
        }
    }
};
</script>

<style></style>
