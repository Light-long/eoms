<template>
	<div class="app-container">
		<div align="center">
			<el-form :model="dataForm" ref="dataForm" :inline="true" label-width="80px">
				<el-form-item label="考勤日期" prop="date" label-width="80px">
					<el-date-picker
							v-model="dataForm.date"
							type="date"
							placeholder="选择日期"
							class="input"
							size="small"
					></el-date-picker>
				</el-form-item>
				<el-form-item label="类型" prop="type" label-width="60px">
					<el-select v-model="dataForm.type" class="input" placeholder="考勤类型" size="small" clearable="clearable">
						<el-option label="签到" value="1" />
						<el-option label="签退" value="2" />
					</el-select>
				</el-form-item>
				<el-form-item style="margin-left: 20px">
					<el-button type="primary" icon="el-icon-search" size="mini" @click="searchHandle">搜索</el-button>
					<el-button icon="el-icon-refresh" size="mini" @click="resetForm">重置</el-button>
				</el-form-item>
			</el-form>
		</div>

		<el-row :gutter="15" class="mb8" style="margin-bottom: 10px">
			<el-col :span="1.5">
				<el-button
						type="common"
						plain
						icon="el-icon-download"
						size="mini"
						:disabled="multiple"
						@click="exportData()"
				>导出</el-button>
			</el-col>
			<el-col :span="1.5">
				<el-button type="primary" plain icon="el-icon-download" size="mini" @click="exportDataAll()">导出全部</el-button>
			</el-col>
		</el-row>

		<el-table v-loading="dataListLoading" :data="dataList" @selection-change="selectionChangeHandle" :header-cell-style="{background:'#eef1f6',color:'#606266'}">
			<el-table-column type="selection" width="60" align="center" />
			<el-table-column type="index" header-align="center" align="center" min-width="50px" label="序号">
				<template #default="scope">
					<span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
				</template>
			</el-table-column>
			<el-table-column label="日期" min-width="120px" align="center" prop="date" :show-overflow-tooltip="true"/>
			<el-table-column label="考勤类型" align="center" prop="type" min-width="100px">
				<template #default="scope">
					<el-tag type="primary" v-if="scope.row.type === '签到'">{{scope.row.type}}</el-tag>
					<el-tag type="success" v-if="scope.row.type === '签退'">{{scope.row.type}}</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="createTime" header-align="center" align="center" min-width="150px" label="考勤时间" />
			<el-table-column label="状态" align="center" prop="status" min-width="120px">
				<template #default="scope">
					<el-tag type="success" v-if="scope.row.status === '正常'">{{scope.row.status}}</el-tag>
					<el-tag type="danger" v-if="scope.row.status === '迟到'">{{scope.row.status}}</el-tag>
					<el-tag type="danger" v-if="scope.row.status === '早退'">{{scope.row.status}}</el-tag>
					<el-tag type="primary" v-if="scope.row.status === '加班'">{{scope.row.status}}</el-tag>
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
</template>

<script>
export default {
	data() {
		return {
			dataForm: {
				date: null,
				type: null
			},
			dataList: [],
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			dataListLoading: false,
			// 非单个禁用
			single: true,
			// 非多个禁用
			multiple: true
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
				date: that.dataForm.date,
				type: that.dataForm.type,
				page: that.pageIndex,
				length: that.pageSize
			}
			if (that.dataForm.date != null && that.dataForm.date !== '') {
				data.date = dayjs(that.dataForm.date).format('YYYY-MM-DD')
			}
			that.$http('/attendance/searchAttendanceRecord', 'POST', data, true, function (resp) {
				let page = resp.page
				for (let one of page.list) {
					if (one.type === 1) {
						one.type = '签到'
						if (one.status === 1) {
							one.status = '正常'
						} else if (one.status === 2) {
							one.status = '迟到'
						}
					} else if (one.type === 2) {
						one.type = '签退'
						if (one.status === 1) {
							one.status = '正常'
						} else if (one.status === 2) {
							one.status = '早退'
						} else if (one.status === 3) {
							one.status = '加班'
						}
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
					that.loadDateList()
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
			this.loadDateList()
		},
		// 导出选中数据
		exportData: function() {
			let attendanceList = []
			for (let i = 0; i < this.dataListSelections.length; i++) {
				attendanceList[i] = this.dataListSelections[i]
			}
			let data = {
				title: "考勤数据表",
				data: JSON.stringify(attendanceList)
			}
			console.log(data)
			let that = this
			that.$http('/excel/exportAttendanceExcel', 'POST', data, true, function (resp) {
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
		// 导出全部数据
		exportDataAll: function () {
			let attendanceList = []
			for (let i = 0; i < this.dataList.length; i++) {
				attendanceList[i] = this.dataList[i]
			}
			let data = {
				title: "考勤数据表",
				data: JSON.stringify(attendanceList)
			}
			console.log(data)
			let that = this
			that.$http('/excel/exportAttendanceExcel', 'POST', data, true, function (resp) {
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

<style lang="less" scoped="scoped"></style>
