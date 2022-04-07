<template>
	<div v-if="isAuth(['ROOT', 'WORKFLOW:APPROVAL', 'FILE:ARCHIVE'])">
		<div align="center">
			<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="searchHandle()">
				<el-form-item prop="creatorName" label="申请人">
					<el-input v-model="dataForm.creatorName" size="small" placeholder="申请人" clearable="clearable" />
				</el-form-item>
				<el-form-item prop="type" label="申请类别">
					<el-select v-model="dataForm.type" placeholder="类别" size="small" clearable="clearable">
						<el-option label="会议申请" value="会议申请"></el-option>
						<el-option label="员工请假" value="员工请假"></el-option>
						<el-option label="报销申请" value="报销申请"></el-option>
					</el-select>
				</el-form-item>
				<el-form-item prop="instanceId" label="实例ID">
					<el-input v-model="dataForm.instanceId" size="small" placeholder="实例编号" clearable="clearable" />
				</el-form-item>
				<el-form-item>
					<el-button type="primary" size="small" @click="searchHandle()">查询</el-button>
					<el-button type="common" size="small" @click="reset">重置</el-button>
				</el-form-item>
				<el-form-item class="mold">
					<el-radio-group v-model="dataForm.status" size="small" @change="searchHandle()">
						<el-radio-button label="待审批"></el-radio-button>
						<el-radio-button label="已审批"></el-radio-button>
						<el-radio-button label="已结束"></el-radio-button>
					</el-radio-group>
				</el-form-item>
			</el-form>
		</div>
		<el-table
			:header-cell-style="{background:'#eef1f6',color:'#606266'}"
			ref="approvalTable"
			:data="dataList"
			border="border"
			v-loading="dataListLoading"
			cell-style="padding: 4px 0"
			size="medium"
			style="width: 100%;"
			@expand-change="expand"
			:row-key="getRowKeys"
			:expand-row-keys="expands"
		>
			<el-table-column prop="remark" header-align="center" align="center" type="expand">
				<template #default="scope">
					<div class="content-container">
						<table class="content-table">
							<tbody v-if="scope.row.type === '会议申请'">
								<tr>
									<th><span>主题</span></th>
									<td :title="scope.row.title">{{ scope.row.title }}</td>
								</tr>
								<tr>
									<th><span>内容</span></th>
									<td :title="content.desc">{{ content.desc }}</td>
								</tr>
								<tr>
									<th><span>日期</span></th>
									<td>{{ content.date }}&nbsp;&nbsp;&nbsp;{{ content.start }} ~ {{ content.end }}</td>
								</tr>
								<tr>
									<th><span>地点</span></th>
									<td>{{ content.place }}</td>
								</tr>
								<tr>
									<th><span>参会人</span></th>
									<td :title="content.members">{{ content.members }}</td>
								</tr>
								<tr>
									<th><span>审批结果</span></th>
									<td>
										<span v-if="scope.row.status!=='已结束'">审批中</span>
										<span v-if="scope.row.status==='已结束'&&scope.row.result==='同意'">已同意</span>
										<span v-if="scope.row.status==='已结束'&&scope.row.result==='不同意'">已拒绝</span>
									</td>
								</tr>
							</tbody>
							<tbody v-if="scope.row.type === '员工请假'">
								<tr>
									<th><span>请假原因</span></th>
									<td :title="content.reason">{{ content.reason }}</td>
								</tr>
								<tr>
									<th><span>请假类型</span></th>
									<td>{{ content.type === 1 ? '病假' : '事假' }}</td>
								</tr>
								<tr>
									<th><span>申请人</span></th>
									<td>{{ content.name }}</td>
								</tr>
								<tr>
									<th><span>起始时间</span></th>
									<td>{{ content.start }}</td>
								</tr>
								<tr>
									<th><span>截止时间</span></th>
									<td>{{ content.end }}</td>
								</tr>
								<tr>
									<th><span>审批结果</span></th>
									<td>
										<span v-if="scope.row.status!=='已结束'">审批中</span>
										<span v-if="scope.row.status==='已结束'&&scope.row.result==='同意'">已同意</span>
										<span v-if="scope.row.status==='已结束'&&scope.row.result==='不同意'">已拒绝</span>
									</td>
								</tr>
							</tbody>
							<tbody v-if="scope.row.type === '报销申请'">
								<tr>
									<th><span>申请人</span></th>
									<td>{{ content.name }}</td>
								</tr>
								<tr>
									<th><span>报销金额</span></th>
									<td>{{ content.amount }}元</td>
								</tr>
								<tr>
									<th><span>借款金额</span></th>
									<td>{{ content.anleihen }}元</td>
								</tr>
								<tr>
									<th><span>实际金额</span></th>
									<td>{{ content.balance }}元</td>
								</tr>
								<tr>
									<th><span>报销类型</span></th>
									<td>{{ content.type }}</td>
								</tr>
								<tr>
									<th><span>审批结果</span></th>
									<td>
										<span v-if="scope.row.status!=='已结束'">审批中</span>
										<span v-if="scope.row.status==='已结束'&&scope.row.result==='同意'">已同意</span>
										<span v-if="scope.row.status==='已结束'&&scope.row.result==='不同意'">已拒绝</span>
									</td>
								</tr>
							</tbody>
						</table>
						<el-image
							class="archive"
							v-if="content.hasOwnProperty('files')"
							:src="content.files[0].url"
							:preview-src-list="archiveList"
						></el-image>
						<el-image class="bpmn" :src="bpmnUrl" :preview-src-list="bpmnList"></el-image>
					</div>
				</template>
			</el-table-column>
			<el-table-column
				type="index"
				header-align="center"
				align="center"
				label="序号"
				width="100"
			/>
			<el-table-column prop="title" header-align="center" align="center" label="审批事项" min-width="200"/>
			<el-table-column prop="type" header-align="center" align="center" label="类别" min-width="180" />
			<el-table-column prop="creatorName" header-align="center" align="center" label="申请人" min-width="150" />
			<el-table-column prop="createDate" header-align="center" align="center" label="发起日期" min-width="180" />
			<el-table-column prop="status" header-align="center" align="center" label="状态" min-width="150">
				<template #default="scope">
					<span v-if="scope.row.status !== '已结束'" style="color: orange;">审批中</span>
					<span v-if="scope.row.status === '已结束' && scope.row.result === '同意'" style="color: #17B3A3;">已同意</span>
					<span v-if="scope.row.status === '已结束' && scope.row.result === '不同意'" style="color: #f56c6c;">已拒绝</span>
				</template>
			</el-table-column>
			<el-table-column header-align="center" align="center" width="150" label="操作">
				<template #default="scope">
					<el-button
						type="text"
						size="medium"
						v-if="isAuth(['ROOT', 'WORKFLOW:APPROVAL']) && dataForm.status === '待审批' && !scope.row.filing"
						@click="approveHandle(scope.row.taskId)"
					>
						审批
					</el-button>
					<el-button
						type="text"
						size="medium"
						v-if="dataForm.status !== '待审批'"
						@click="viewHandle(scope.row)"
					>
						查看
					</el-button>
					<el-button
						type="text"
						size="medium"
						v-if="
							isAuth(['FILE:ARCHIVE']) &&
								scope.row.filing
						"
						@click="archive(scope.row.taskId)"
					>
						归档
					</el-button>
				</template>
			</el-table-column>
		</el-table>
		<el-pagination
			@size-change="sizeChangeHandle"
			@current-change="currentChangeHandle"
			:current-page="pageIndex"
			:page-sizes="[10, 20, 50]"
			:page-size="pageSize"
			:total="totalCount"
			layout="total, sizes, prev, pager, next, jumper"
		></el-pagination>
		<archive v-if="archiveVisible" ref="archive" @refreshDataList="loadDataList"></archive>
	</div>
</template>

<script>
	import Archive from '../archive/archive.vue';

	export default {
	components: {
		Archive
	},
	data: function() {
		return {
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			dataListLoading: false,
			archiveVisible: false,
			dataForm: {
				creatorName: null,
				type: null,
				status: '待审批',
				instanceId: null
			},
			dataList: [],
			content: {},
			bpmnUrl: null,
			bpmnList: [],
			archiveList: [],
			dataRule: {
				creatorName: [{ required: false, pattern: '^[\u4e00-\u9fa5]{2,20}$', message: '姓名格式错误' }],
				instanceId: [{ required: false, pattern: '^[0-9A-Za-z\\-]{36}$', message: '实例编号格式错误' }]
			},
			expands: [],
			//如果数组中保存了某行记录的唯一值，那么这行的折叠面板展开
			getRowKeys(row) {
				return row.taskId
			}
		};
	},
	created: function() {
		this.loadDataList()
	},
	methods: {
		loadDataList: function () {
			let that = this
			that.dataListLoading = true
			let data = {
				creatorName: that.dataForm.creatorName,
				type: that.dataForm.type,
				status: that.dataForm.status,
				instanceId: that.dataForm.instanceId,
				page: that.pageIndex,
				length: that.pageSize
			}
			that.$http('/approval/searchTaskByPage', "POST", data, true, function (resp) {
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
		searchHandle: function () {
			this.$refs['dataForm'].validate(valid => {
				if (valid) {
					this.$refs['dataForm'].clearValidate()
					if (this.dataForm.creatorName === '') {
						this.dataForm.creatorName = null
					}
					if (this.dataForm.instanceId === '') {
						this.dataForm.instanceId = null
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
			this.dataForm.status = '待审批'
			this.loadDataList()
		},
		// 点击展开按钮，查询
		expand: function (row, expandedRows) {
			let that = this
			// 判断是否展开了折叠面板
			if (expandedRows.length > 0) {
				// 关闭所有的折叠面板
				that.expands = []
				if (row) {
					// 展开你想展开的面板
					that.expands.push(row.taskId)
					let data = {
						instanceId: row.processId,
						type: row.type,
						status: row.status
					}
					that.bpmnUrl = this.$baseUrl + 'approval/searchApprovalBPMN'
							+ '?instanceId=' + row.processId
							+ '&time=' + new Date().getTime()
					that.bpmnList = [that.bpmnUrl]
					that.$http('/approval/searchApprovalContent', "POST", data, true, function (resp) {
						let content = resp.content
						that.content = content
						//如果返回工作流实例存在files，说明工作流实例绑定了归档文件
						if (content.hasOwnProperty('files')) {
							for (let one of content.files) {
								that.archiveList.push(one.url)
							}
						}
						if (row.type === '报销申请') {
							if (content.typeId === 1) {
								content.type = '普通报销'
							} else if (content.typeId === 2) {
								content.type = '差旅报销'
							}
						}
					})
				}
			} else {
				that.expands = []
			}
		},
		approval: function (taskId, approval) {
			let that = this
			that.dataListLoading = true
			let data = {
				taskId: taskId,
				approval: approval
			}
			that.$http('/approval/approvalTask', "POST", data, true, function (resp) {
				that.pageIndex = 1
				that.loadDataList()
				that.$message({
					message: '审批成功',
					type: 'success',
					duration: 1200
				});
			})
		},
		// 点击审批按钮
		approveHandle: function (taskId) {
			let that = this
			that.$confirm('请选择对该条申请的处理意见', '提示', {
				confirmButtonText: '同意',
				cancelButtonText: '否决',
				type: 'warning',
				// 去除关闭图标
				distinguishCancelAndClose: true,
				callback: function(action) {
					if (action === 'confirm') {
						that.approval(taskId, '同意');
					} else if (action === 'cancel') {
						that.approval(taskId, '不同意');
					}
				}
			})
		},
		// 和点击展开一样效果
		viewHandle: function (row) {
			this.$refs.approvalTable.toggleRowExpansion(row, true)
		},
		// 归档
		archive: function (taskId) {
			this.archiveVisible = true
			this.$nextTick(() => {
				this.$refs.archive.init(taskId)
			})
		}
	}
};
</script>

<style lang="less" scoped="scoped">
	@import url('approval.less');
</style>
