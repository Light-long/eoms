<template>
    <div class="app-container">
        <el-form :model="dataForm" ref="dataForm" :inline="true" label-width="68px" :rules="dataRule">
            <el-form-item>
                <el-select
                        v-model="dataForm.deptId"
                        class="input"
                        placeholder="部门名称"
                        size="small"
                        clearable="clearable"
                >
                    <el-option v-for="one in deptList" :label="one.deptName" :value="one.id" />
                </el-select>
            </el-form-item>
            <el-form-item prop="name">
                <el-input
                        v-model="dataForm.name"
                        placeholder="姓名"
                        size="small"
                        class="input"
                        clearable="clearable"
                        @keyup.enter.native="searchHandle"
                />
            </el-form-item>
            <el-form-item>
                <el-select v-model="dataForm.sex" class="input" placeholder="性别" size="small" clearable="clearable">
                    <el-option label="男" value="男" />
                    <el-option label="女" value="女" />
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
                        icon="el-icon-download"
                        size="mini"
                        @click="exportDataAll()"
                >导出全部</el-button>
            </el-col>
        </el-row>
        <el-table v-loading="dataListLoading" :data="dataList" :header-cell-style="{background:'#eef1f6',color:'#606266'}">
            <el-table-column type="index" header-align="center" align="center" min-width="50px" label="序号">
                <template #default="scope">
                    <span>{{ scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column label="部门名称" min-width="100px" align="center" prop="deptName" :show-overflow-tooltip="true"/>
            <el-table-column label="姓名" align="center" prop="name" min-width="100px"></el-table-column>
            <el-table-column label="性别" align="center" prop="isTopping" min-width="100px">
                <template #default="scope">
                    <el-tag type="success" v-if="scope.row.sex === '男'">{{scope.row.sex}}</el-tag>
                    <el-tag type="primary" v-if="scope.row.sex === '女'">{{scope.row.sex}}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="电话" align="center" prop="tel" min-width="150px"></el-table-column>
            <el-table-column label="电子邮箱" align="center" prop="email" min-width="200px"></el-table-column>
            <el-table-column label="操作" align="center" min-width="150px" class-name="small-padding fixed-width">
                <template #default="scope">
                    <el-button
                            v-if="scope.row.tel != null"
                            class="copy-btn"
                            :data-clipboard-text="scope.row.tel"
                            size="medium"
                            type="text"
                            icon="el-icon-phone"
                            @click="copy"
                    >复制电话</el-button>
                    <el-button
                            v-if="scope.row.email != null"
                            class="copy-btn"
                            :data-clipboard-text="scope.row.email"
                            size="medium"
                            type="text"
                            icon="el-icon-message"
                            @click="copy"
                    >复制邮箱</el-button>
                </template>
            </el-table-column>
        </el-table>
    </div>
</template>

<script>
    import Clipboard from 'clipboard'

    export default {
        name: "mail-list",
        data: function () {
            return {
                dataListLoading: false,
                dataForm: {
                    deptId: null,
                    name: null,
                    sex: null
                },
                dataList: [],
                deptList: [],
                dataRule: {
                    name: [{ required: false, pattern: '^[\u4e00-\u9fa5]{1,10}$', message: '姓名格式错误' }]
                }
            }
        },
        created: function() {
            this.loadDeptList()
            this.loadDataList()
        },
        methods: {
            // 加载所有部门信息
            loadDeptList: function() {
                let that = this;
                that.$http('/dept/searchAllDept', 'GET', null, true, function(resp) {
                    that.deptList = resp.deptList
                });
            },
            loadDataList: function() {
                let that = this
                that.dataListLoading = true
                let data = {
                    name: that.dataForm.name,
                    deptId: that.dataForm.deptId,
                    sex: that.dataForm.sex
                }
                that.$http('/user/searchMailList', 'POST', data, true, function (resp) {
                    that.dataList = resp.mailList
                    that.dataListLoading = false
                })
            },
            // 条件查询
            searchHandle: function() {
                this.$refs['dataForm'].validate(valid => {
                    if (valid) {
                        this.$refs['dataForm'].clearValidate()
                        if (this.dataForm.name === '') {
                            this.dataForm.name = null
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
            copy: function () {
                let clipboard = new Clipboard('.copy-btn')
                clipboard.on('success', e => {
                    this.$message({
                        message: '复制成功',
                        type: 'success',
                        duration: 1200
                    });
                    clipboard.destroy() // 释放内存
                })
                clipboard.on('error', e => {
                    this.$message({
                        message: '复制失败',
                        type: 'error',
                        duration: 1200
                    });
                    clipboard.destroy() // 释放内存
                })
            },
            exportDataAll: function () {
                //处理封装userList对象
                let infoList = []
                for (let i = 0; i < this.dataList.length; i++) {
                    infoList[i] = this.dataList[i]
                }
                let data = {
                    title: "通讯录",
                    data: JSON.stringify(infoList)
                }
                let that = this
                that.$http('/excel/exportMailListExcel', 'POST', data, true, function (resp) {
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
            },
        }
    }
</script>

<style lang="less" scoped>

</style>