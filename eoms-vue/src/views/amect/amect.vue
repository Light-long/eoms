<template>
	<div>
		<div align="center">
			<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
				<el-form-item v-if="isAuth(['ROOT'])">
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
				<el-form-item prop="name" v-if="isAuth(['ROOT', 'AMECT:SELECT'])">
					<el-input
							v-model="dataForm.name"
							placeholder="姓名"
							size="small"
							class="input"
							clearable="clearable"
					/>
				</el-form-item>
				<el-form-item>
					<el-select
							v-model="dataForm.typeId"
							class="input"
							placeholder="罚款类型"
							size="small"
							clearable="clearable"
					>
						<el-option v-for="one in amectTypeList" :label="one.type" :value="one.id" />
					</el-select>
				</el-form-item>
				<el-form-item>
					<el-date-picker
							v-model="dataForm.date"
							type="daterange"
							range-separator="~"
							start-placeholder="开始日期"
							end-placeholder="结束日期"
							size="small"
					></el-date-picker>
				</el-form-item>
				<el-form-item>
					<el-select
							v-model="dataForm.status"
							class="input"
							placeholder="缴纳状态"
							size="small"
							clearable="clearable"
					>
						<el-option label="未缴纳" value="1" />
						<el-option label="已缴纳" value="2" />
					</el-select>
				</el-form-item>
			</el-form>
		</div>

		<el-row :gutter="15" class="mb8" style="margin-bottom: 10px">
			<el-col :span="1.5">
				<el-button
						:disabled="!isAuth(['ROOT', 'AMECT:INSERT'])"
						type="success"
						plain
						icon="el-icon-plus"
						size="mini"
						@click="addHandle()"
				>新增</el-button>
			</el-col>
			<el-col :span="1.5">
				<el-button
						type="primary"
						plain
						icon="el-icon-edit"
						size="mini"
						:disabled="single || !isAuth(['ROOT', 'AMECT:UPDATE'])"
						@click="updateHandle(dataListSelections.map(item => item.id)[0])"
				>修改</el-button>
			</el-col>
			<el-col :span="1.5">
				<el-button
						type="danger"
						plain
						icon="el-icon-delete"
						size="mini"
						:disabled="multiple || !isAuth(['ROOT', 'AMECT:DELETE'])"
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
			<el-col :span="1.5">
				<el-button
						:disabled="!isAuth(['ROOT', 'AMECT:SELECT'])"
						type="warning"
						plain
						icon="el-icon-zoom-in"
						size="mini"
						@click="reportHandle()"
				>查看报告</el-button>
			</el-col>
			<el-col :span="1.5" style="margin-left: 570px">
				<el-button type="primary" icon="el-icon-search" size="mini" @click="searchHandle">搜索</el-button>
				<el-button icon="el-icon-refresh" size="mini" @click="reset">重置</el-button>
			</el-col>
		</el-row>

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
			<el-table-column width="40px" prop="reason" header-align="center" align="center" type="expand">
				<template #default="scope">
					罚款原因：{{ scope.row.reason }}
				</template>
			</el-table-column>
			<el-table-column type="index" header-align="center" align="center" width="100" label="序号">
				<template #default="scope">
					<span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
				</template>
			</el-table-column>
			<el-table-column prop="type" header-align="center" align="center" label="罚款类型" />
			<el-table-column prop="name" header-align="center" align="center" label="当事人" />
			<el-table-column prop="deptName" header-align="center" align="center" label="所属部门" />
			<el-table-column header-align="center" align="center" label="罚款金额">
				<template #default="scope">
					<span>{{ scope.row.amount }}元</span>
				</template>
			</el-table-column>
			<el-table-column prop="status" header-align="center" align="center" label="状态">
				<template #default="scope">
					<span v-if="scope.row.status === '未缴纳'" style="color: orange;">未缴纳</span>
					<span v-if="scope.row.status === '已缴纳'" style="color: #17B3A3;">已缴纳</span>
				</template>
			</el-table-column>
			<el-table-column prop="createTime" header-align="center" align="center" label="日期时间" />
			<el-table-column header-align="center" align="center" width="150" label="操作">
				<template #default="scope">
					<el-button
						v-if="isAuth(['ROOT', 'AMECT:DELETE'])"
						icon="el-icon-delete"
						type="text"
						size="medium"
						:disabled="!(isAuth(['ROOT', 'AMECT:DELETE']) && scope.row.status !== '已缴纳')"
						@click="deleteHandle(scope.row.id)"
					>
						删除
					</el-button>
					<el-button
						v-if="scope.row.status === '未缴纳'"
						icon="el-icon-circle-check"
						type="text"
						size="medium"
						:disabled="!(scope.row.mine === 'true' && scope.row.status === '未缴纳')"
						@click="payHandle(scope.row.id)"
					>
						交款
					</el-button>
					<el-button
							v-if="scope.row.status === '已缴纳'"
							icon="el-icon-circle-check"
							type="text"
							size="medium"
							:disabled="true"
							@click="payHandle(scope.row.id)"
					>
						已交款
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
		<pay v-if="payVisible" ref="pay" @refreshDataList="loadDataList"></pay>
	</div>
</template>

<script>
	import dayjs from 'dayjs';
	import AddOrUpdate from './amect-add-or-update.vue';
	import Pay from './amect-pay.vue';

	export default {
	components: { AddOrUpdate, Pay },
	data: function() {
		return {
			dataForm: {
				name: null,
				deptId: null,
				typeId: null,
				status: null,
				date: null
			},
			deptList: [],
			amectTypeList: [],
			dataList: [],
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			dataListLoading: false,
			dataListSelections: [],
			dataRule: {
				name: [{ required: false, pattern: '^[\u4e00-\u9fa5]{1,10}$', message: '姓名格式错误' }]
			},
			addOrUpdateVisible: false,
			payVisible: false,
			// 非单个禁用
			single: true,
			// 非多个禁用
			multiple: true
		};
	},
	methods: {
		selectionChangeHandle: function(val) {
			this.dataListSelections = val
			this.single = val.length !== 1
			this.multiple = !val.length
		},
		loadDeptList: function() {
			let that = this;
			that.$http('/dept/searchAllDept', 'GET', null, true, function(resp) {
				that.deptList = resp.deptList;
			});
		},
		loadAmectTypeList: function() {
			let that = this;
			that.$http('/amectType/searchAllAmectType', 'GET', {}, true, function(resp) {
				that.amectTypeList = resp.list;
			});
		},
		loadDataList: function () {
			let that = this
			that.dataListLoading = true
			let data = {
				name: that.dataForm.name,
				deptId: that.dataForm.deptId,
				typeId: that.dataForm.typeId,
				status: that.dataForm.status,
				page: that.pageIndex,
				length: that.pageSize
			}
			if (that.dataForm.date != null && that.dataForm.date.length === 2) {
				let startDate = that.dataForm.date[0]
				let endDate = that.dataForm.date[1]
				data.startDate = dayjs(startDate).format("YYYY-MM-DD")
				data.endDate = dayjs(endDate).format("YYYY-MM-DD")
			}
			that.$http('/amect/searchAmectByPage', "POST", data, true, function (resp) {
				let page = resp.page
				for (let one of page.list) {
					if (one.status === 1) {
						one.status = '未缴纳'
					} else if (one.status === 2) {
						one.status = '已缴纳'
					}
				}
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
					if (this.dataForm.name === '') {
						this.dataForm.name = null
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
			this.dataForm = []
			this.pageIndex = 1
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
		selectable: function (row) {
			return row.status !== '已缴纳';
		},
		deleteHandle: function (id) {
			let that = this
			let ids = id ? [id] : this.dataListSelections.map(item => item.id)
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
					that.$http('/amect/deleteAmectByIds', "POST", {ids: ids}, true, function (resp) {
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
		},
		payHandle: function (id) {
			this.payVisible = true
			this.$nextTick(() => {
				this.$refs.pay.init(id)
			})
		},
		reportHandle: function () {
			this.$router.push({name: 'AmectReport'})
		},
		exportDataAll: function () {
			let amectList = []
			for (let i = 0; i < this.dataList.length; i++) {
				amectList[i] = this.dataList[i]
			}
			let data = {
				title: "罚款数据表",
				data: JSON.stringify(amectList)
			}
			console.log(data)
			let that = this
			that.$http('/excel/exportAmectExcel', 'POST', data, true, function (resp) {
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
	},
	created: function() {
		this.loadDeptList()
		this.loadAmectTypeList()
		this.loadDataList()
	}
};
</script>

<style></style>
