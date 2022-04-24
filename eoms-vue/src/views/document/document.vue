<template>
	<div class="app-container">
		<div align="center">
			<el-form :model="dataForm" ref="dataForm" :inline="true" label-width="80px">
				<el-form-item prop="name" label="文件名">
					<el-input
							v-model="dataForm.documentName"
							class="input"
							placeholder="文件名"
							size="small"
							clearable="clearable"
					></el-input>
				</el-form-item>
				<el-form-item label="上传日期" prop="uploadDate">
					<el-date-picker
							v-model="dataForm.uploadDate"
							style="width: 160px;"
							type="date"
							size="small"
							placeholder="上传日期"
							clearable="clearable"
					></el-date-picker>
				</el-form-item>
				<el-form-item style="margin-left: 10px">
					<el-button type="primary" icon="el-icon-search" size="mini" @click="searchHandle">搜索</el-button>
					<el-button icon="el-icon-refresh" size="mini" @click="resetForm">重置</el-button>
				</el-form-item>
				<el-form-item class="mold">
					<el-radio-group v-model="dataForm.mold" size="small" @change="changeHandle">
						<el-radio-button label="公开"></el-radio-button>
						<el-radio-button label="私有"></el-radio-button>
					</el-radio-group>
				</el-form-item>
			</el-form>
		</div>

		<el-row :gutter="15" class="mb8" style="margin-bottom: 10px">
			<el-col :span="1.5">
				<el-button type="primary" plain icon="el-icon-upload" size="mini" @click="uploadFile()">上传文件</el-button>
			</el-col>
		</el-row>

		<el-table v-loading="dataListLoading" :data="dataList" @selection-change="selectionChangeHandle" :header-cell-style="{background:'#eef1f6',color:'#606266'}">
			<el-table-column type="selection" width="60" align="center" />
			<el-table-column type="index" header-align="center" align="center" min-width="50px" label="序号">
				<template #default="scope">
					<span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
				</template>
			</el-table-column>
			<el-table-column label="文件名" min-width="120px" align="center" prop="documentName" :show-overflow-tooltip="true"/>
			<el-table-column label="上传人员" min-width="120px" align="center" prop="userName" :show-overflow-tooltip="true"/>
			<el-table-column label="上传日期" min-width="120px" align="center" prop="uploadDate" :show-overflow-tooltip="true"/>
			<el-table-column label="是否公开" align="center" prop="status" min-width="120px">
				<template #default="scope">
					<el-tag type="danger" v-if="scope.row.isPublic === '私有'">{{scope.row.isPublic}}</el-tag>
					<el-tag type="primary" v-if="scope.row.isPublic === '公开'">{{scope.row.isPublic}}</el-tag>
				</template>
			</el-table-column>
			<el-table-column label="上传时间" min-width="150px" align="center" prop="uploadTime" :show-overflow-tooltip="true"/>
			<el-table-column header-align="center" align="center" label="操作" min-width="150">
				<template #default="scope">
					<el-button
							type="success"
							icon="el-icon-download"
							size="small"
							@click="downloadDocument(scope.row.url)"
					>
					下载
					</el-button>
					<el-button
							type="danger"
							icon="el-icon-delete"
							size="small"
							v-if="scope.row.mine == 'true'"
							@click="deleteDocument(scope.row.id)"
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
		<DocumentUpload v-if="uploadVisible" ref="documentUpload" @refreshDataList="loadDateList"></DocumentUpload>
	</div>
</template>

<script>
	import DocumentUpload from './document-upload.vue'
export default {
	components: {
		DocumentUpload
	},
	data() {
		return {
			dataForm: {
				documentName: null,
				uploadDate: null,
				mold: '公开'
			},
			dataList: [],
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			dataListLoading: false,
			// 非单个禁用
			single: true,
			// 非多个禁用
			multiple: true,
			uploadVisible: false
		};
	},
	created: function() {
		this.loadDateList()
	},
	methods: {
		// 加载数据
		loadDateList: function() {
			let that = this
			that.dataListLoading = true
			let data = {
				documentName: that.dataForm.documentName,
				uploadDate: that.dataForm.uploadDate,
				page: that.pageIndex,
				length: that.pageSize,
				isPublic: that.dataForm.mold === '公开' ? 1 : 0
			}
			if (that.dataForm.uploadDate != null) {
				data.uploadDate = dayjs(that.dataForm.uploadDate).format('YYYY-MM-DD')
			}
			that.$http('/document/searchDocumentByPage', 'POST', data, true, function (resp) {
				let page = resp.page
				for (let one of page.list) {
					if (one.isPublic === 1) {
						one.isPublic = '公开'
					} else if (one.isPublic === 0) {
						one.isPublic = '私有'
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
					that.$refs['dataForm'].clearValidate()
					if (that.pageIndex !== 1) {
						that.pageIndex = 1
					}
					that.loadDateList()
				} else {
					return false
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
			this.dataForm = []
			this.dataForm.mold = '公开'
			this.pageIndex = 1
			this.loadDateList()
		},
		changeHandle: function() {
			this.searchHandle()
		},
		// 上传
		uploadFile: function () {
			this.uploadVisible = true
			this.$nextTick(() => {
				this.$refs.documentUpload.init()
			})
		},
		// 下载
		downloadDocument: function (url) {
			window.open(url)
		},
		// 删除
		deleteDocument: function (id) {
			let that = this
			that.$confirm(`确定要删除选中的记录？`, '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '点错了',
				type: 'warning'
			}).then(() => {
				that.$http('/document/deleteDocument', 'POST', {id: id}, true, function (resp) {
					if (resp.rows === 1) {
						that.$message({
							message: '删除成功',
							type: 'success',
							duration: 1200
						});
						that.loadDateList()
					} else {
						that.$message({
							message: '删除失败',
							type: 'error',
							duration: 1200
						});
					}
				})
			})
		}
	}
};
</script>

<style lang="less" scoped="scoped">
	.mold{
		float: right;
		margin-right: 0 !important;
	}
</style>
