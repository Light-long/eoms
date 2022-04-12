<template>
	<div>
		<div align="center">
			<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
				<el-form-item label="部门" v-if="isAuth(['ROOT'])">
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
				<el-form-item prop="name" label="申请人" v-if="isAuth(['ROOT', 'LEAVE:SELECT'])">
					<el-input
							v-model="dataForm.name"
							placeholder="姓名"
							size="small"
							class="input"
							clearable="clearable"
					/>
				</el-form-item>
				<el-form-item label="状态">
					<el-select
							v-model="dataForm.status"
							class="input"
							placeholder="状态"
							size="small"
							clearable="clearable"
					>
						<el-option label="待审批" value="1" />
						<el-option label="已否决" value="2" />
						<el-option label="已同意" value="3" />
						<el-option label="已归档" value="4" />
					</el-select>
				</el-form-item>
				<el-form-item label="日期">
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
					<el-button size="small" type="primary" @click="searchHandle()">查询</el-button>
					<el-button size="small" type="common" @click="reset()">重置</el-button>
				</el-form-item>
			</el-form>
		</div>

		<el-row :gutter="15" class="mb8" style="margin-bottom: 10px">
			<el-col :span="1.5">
				<el-button
						plain
						size="small"
						type="success"
						@click="addHandle()">
					申请报销
				</el-button>
			</el-col>
		</el-row>

		<el-table
			:data="dataList"
			border
			v-loading="dataListLoading"
			cell-style="padding: 4px 0"
			style="width: 100%;"
			size="medium"
			:header-cell-style="{background:'#eef1f6',color:'#606266'}"
		>
			<el-table-column
				width="40px"
				prop="content"
				header-align="center"
				align="center"
				type="expand"
			>
				<template #default="scope">
					<div class="reim-table">
						<div class="row">
							<div class="title">序号</div>
							<div class="title">类别</div>
							<div class="title">报销项目</div>
							<div class="title">备注信息</div>
							<div class="title">金额</div>
						</div>
						<div class="row" v-for="(one, $index) in scope.row.content">
							<div class="col">{{ $index + 1 }}</div>
							<div class="col">{{ one.type }}</div>
							<div class="col">{{ one.title }}</div>
							<div class="col">{{ one.desc }}</div>
							<div class="col">￥{{ one.money }}</div>
						</div>
					</div>
				</template>
			</el-table-column>
			<el-table-column
				type="index"
				header-align="center"
				align="center"
				min-width="80px"
				label="序号"
			>
				<template #default="scope">
					<span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
				</template>
			</el-table-column>
			<el-table-column
				prop="name"
				header-align="center"
				align="center"
				label="申请人"
				min-width="120px"
			/>
			<el-table-column
				prop="deptName"
				header-align="center"
				align="center"
				label="所属部门"
				min-width="120px"
			/>
			<el-table-column header-align="center" align="center" label="报销金额" min-width="120px">
				<template #default="scope">
					<span>{{ scope.row.amount }}元</span>
				</template>
			</el-table-column>
			<el-table-column header-align="center" align="center" label="借款金额" min-width="120px">
				<template #default="scope">
					<span>{{ scope.row.anleihen }}元</span>
				</template>
			</el-table-column>
			<el-table-column header-align="center" align="center" label="实际报销" min-width="120px">
				<template #default="scope">
					<span>{{ scope.row.balance }}元</span>
				</template>
			</el-table-column>
			<el-table-column prop="status" header-align="center" align="center" label="状态" min-width="120px">
				<template #default="scope">
					<span v-if="scope.row.status === '待审批'" style="color: orange;">待审批</span>
					<span v-if="scope.row.status === '已否决'" style="color: red;">已否决</span>
					<span v-if="scope.row.status === '已通过'" style="color: green;">已通过</span>
					<span v-if="scope.row.status === '已归档'" style="color: grey;">已归档</span>
				</template>
			</el-table-column>
			<el-table-column
				prop="createTime"
				header-align="center"
				align="center"
				label="申请日期"
				min-width="150px"
			/>
			<el-table-column header-align="center" align="center" label="报销单" width="120px">
				<template #default="scope">
					<el-button
							type="text"
							size="medium"
							@click="pdfHandle(scope.row.id)">
						报销单
					</el-button>
				</template>
			</el-table-column>
			<el-table-column header-align="center" align="center" min-width="100px" label="操作">
				<template #default="scope">
					<el-button
						icon="el-icon-delete"
						type="text"
						size="medium"
						:disabled="!(['待审批', '已否决'].includes(scope.row.status) && scope.row.mine === 'true')"
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
	<add v-if="addVisible" ref="add" @refreshDataList="loadDataList"></add>
	<reim-pdf v-if="pdfVisible" ref="pdf" @refreshDataList="loadDataList"></reim-pdf>
</template>

<script>
import Add from './reim-add.vue';
import dayjs from 'dayjs';
import ReimPdf from './reim_pdf.vue';
export default {
	components: { Add, ReimPdf },
	data: function() {
		return {
			dataForm: {
				name: null,
				deptId: null,
				status: null,
				date: null
			},
			deptList: [],
			dataList: [],
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			dataListLoading: false,
			dataRule: {
				name: [
					{ required: false, pattern: '^[\u4e00-\u9fa5]{1,10}$', message: '姓名格式错误' }
				]
			},
			addVisible: false,
			pdfVisible: false
		};
	},
	methods: {
		loadDeptList: function() {
			let that = this;
			that.$http('/dept/searchAllDept', 'GET', null, true, function(resp) {
				that.deptList = resp.deptList;
			});
		},
		loadDataList: function () {
			let that = this
			that.dataListLoading = true
			let data = {
				name: that.dataForm.name,
				deptId: that.dataForm.deptId,
				status: that.dataForm.status,
				page: that.pageIndex,
				length: that.pageSize
			}
			if (that.dataForm.date != null && that.dataForm.date.length === 2) {
				let startDate = that.dataForm.date[0]
				let endDate = that.dataForm.date[1]
				data.startDate = dayjs(startDate).format('YYYY-MM-DD')
				data.endDate = dayjs(endDate).format('YYYY-MM-DD')
			}
			that.$http('/reim/searchReimByPage', 'POST', data, true, function (resp) {
				let page = resp.page
				let status = {
					1: '待审批',
					2: '已否决',
					3: '已通过',
					4: '已归档',
				}
				for (let one of page.list) {
					one.status = status[one.status]
					one.content = JSON.parse(one.content)
				}
				that.dataList = page.list
				that.totalCount = page.totalCount
				that.dataListLoading = false
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
		reset: function() {
			this.dataForm = {}
			this.loadDataList()

		},
		addHandle: function () {
			this.addVisible = true
			this.$nextTick(() => {
				this.$refs.add.init()
			})
		},
		pdfHandle: function (id) {
			this.pdfVisible = true
			this.$nextTick(() => {
				this.$refs.pdf.init(id)
			})
		},
		deleteHandle: function (id) {
			let that = this
			that.$confirm(`确定要删除选中的记录？`, '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				that.$http('/reim/deleteReimById', 'POST', {id: id}, true, function (resp) {
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
	},
	created: function() {
		this.loadDeptList()
		this.loadDataList()
	}
};
</script>

<style lang="less" scoped="scoped">
	@import url('reim.less');
</style>
