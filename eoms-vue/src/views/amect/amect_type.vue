<template>
	<div class="app-container" v-if="isAuth(['ROOT'])">
		<!--查询表单-->
		<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="80px">
			<el-form-item prop="type" label="罚款类型">
				<el-input
						v-model="dataForm.type"
						placeholder="类型名称"
						size="small"
						class="input"
						clearable="clearable"
						@keyup.enter.native="searchHandle"
				/>
			</el-form-item>
			<el-form-item label="是否内置" prop="systemic">
				<el-select v-model="dataForm.systemic" placeholder="是否内置" clearable size="small">
					<el-option label="是" value="1"></el-option>
					<el-option label="否" value="0"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item>
				<el-button type="primary" icon="el-icon-search" size="mini" @click="searchHandle">搜索</el-button>
				<el-button icon="el-icon-refresh" size="mini" @click="reset">重置</el-button>
			</el-form-item>
		</el-form>

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
		</el-row>

		<el-table v-loading="dataListLoading" :data="dataList" @selection-change="selectionChangeHandle" :header-cell-style="{background:'#eef1f6',color:'#606266'}">
			<el-table-column type="selection" :selectable="selectable" header-align="center" align="center" min-width="80px"/>
			<el-table-column type="index" header-align="center" align="center" min-width="100px" label="序号">
				<template #default="scope">
					<span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
				</template>
			</el-table-column>
			<el-table-column prop="type" header-align="center" min-width="200px" align="center" label="罚款类型" />
			<el-table-column header-align="center" min-width="150px" align="center" label="罚款金额">
				<template #default="scope">
					<span>{{ scope.row.money }}元</span>
				</template>
			</el-table-column>
			<el-table-column header-align="center" min-width="150px" align="center" label="系统内置">
				<template #default="scope">
					<el-tag type="primary" v-if="scope.row.systemic === true">是</el-tag>
					<el-tag type="info" v-if="scope.row.systemic === false">否</el-tag>
				</template>
			</el-table-column>
			<el-table-column header-align="center" min-width="150px" align="center" label="未缴罚款数量">
				<template #default="scope">
					<span>{{ scope.row.notPay === 0 ? '--' : scope.row.notPay + '个' }}</span>
				</template>
			</el-table-column>
			<el-table-column fixed="right" header-align="center" align="center" min-width="200px" label="操作">
				<template #default="scope">
					<el-button
							type="text"
							size="medium"
							icon="el-icon-edit"
							@click="updateHandle(scope.row.id)"
					>
						修改
					</el-button>
					<el-button
						type="text"
						size="medium"
						icon="el-icon-delete"
						:disabled="scope.row.canDelete === 'false'"
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

import AddOrUpdate from './amect_type-add-or-update.vue';

export default {
	components: {
		AddOrUpdate
	},
	data: function() {
		return {
			dataForm: {
				type: null,
				systemic: null
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
				type: [{ required: false, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{1,10}$', message: '类型名称格式错误' }]
			}
		};
	},
	methods: {
		loadDataList: function () {
			let that = this
			that.dataListLoading = true
			let data = {
				type: that.dataForm.type,
				systemic: that.dataForm.systemic,
				page: that.pageIndex,
				length: that.pageSize
			}
			that.$http('/amectType/searchAmectTypeByPage', "POST", data, true, function (resp) {
				let page = resp.page
				that.dataList = page.list
				that.totalCount = page.totalCount
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
			this.$refs['dataForm'].validate(valid => {
				if (valid) {
					this.$refs['dataForm'].clearValidate()
					if (this.dataForm.type === '') {
						this.dataForm.type = null
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
		reset: function () {
			this.dataForm = {}
			this.loadDataList()
		},
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
		selectionChangeHandle: function (val) {
			this.dataListSelections = val
			this.single = val.length !== 1
			this.multiple = !val.length
		},
		selectable: function (row) {
			return row.canDelete === 'true'
		},
		deleteHandle: function (id) {
			let that = this
			let ids = id ? [id] : that.dataListSelections.map(item => item.id)
			if (ids.length === 0) {
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
				}).then(() => {
					that.$http('/amectType/deleteAmectTypeByIds', 'POST', {ids: ids}, true, function (resp) {
						if (resp.rows > 0) {
							that.$message({
								message: '操作成功',
								type: 'success',
								duration: 1200,
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
		}
	},
	created: function() {
		this.loadDataList()
	}
};
</script>

<style lang="less" scoped="scoped"></style>
