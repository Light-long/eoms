<template>
	<div v-if="isAuth(['ROOT'])">
		<div align="center">
			<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
				<el-form-item prop="type">
					<el-input
							v-model="dataForm.type"
							placeholder="类型名称"
							size="medium"
							class="input"
							clearable="clearable"
					/>
				</el-form-item>
				<el-form-item>
					<el-button size="medium" type="primary" @click="searchHandle()">查询</el-button>
					<el-button size="medium" type="common" @click="reset()">重置</el-button>
					<el-button size="medium" type="success" @click="addHandle()">新增</el-button>
					<el-button size="medium" type="danger" @click="deleteHandle()">批量删除</el-button>
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
			<el-table-column
				type="selection"
				:selectable="selectable"
				header-align="center"
				align="center"
				width="50"
			/>
			<el-table-column type="index" header-align="center" align="center" width="100" label="序号">
				<template #default="scope">
					<span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
				</template>
			</el-table-column>
			<el-table-column prop="type" header-align="center" align="center" label="罚款类型" />
			<el-table-column header-align="center" align="center" label="罚款金额">
				<template #default="scope">
					<span>{{ scope.row.money }}元</span>
				</template>
			</el-table-column>
			<el-table-column header-align="center" align="center" label="系统内置">
				<template #default="scope">
					<span>{{ scope.row.systemic ? '是' : '否' }}</span>
				</template>
			</el-table-column>
			<el-table-column header-align="center" align="center" label="未缴罚款数量">
				<template #default="scope">
					<span>{{ scope.row.notPay === 0 ? '--' : scope.row.notPay + '个' }}</span>
				</template>
			</el-table-column>
			<el-table-column fixed="right" header-align="center" align="center" width="150" label="操作">
				<template #default="scope">
					<el-button type="text" size="medium" @click="updateHandle(scope.row.id)">修改</el-button>
					<el-button
						type="text"
						size="medium"
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
				type: null
			},
			dataList: [],
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			dataListLoading: false,
			dataListSelections: [],
			addOrUpdateVisible: false,
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
			this.dataForm.type = null
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
