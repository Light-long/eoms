<template>
	<el-dialog width="780px" :close-on-click-modal="false" v-model="visible" :show-close="false">
		<div id="pdfDom">
			<h2 class="title">员工请假单</h2>
			<table class="leave-table">
				<tr align="center">
					<td width="14%">姓名</td>
					<td width="16%">{{ name }}</td>
					<td width="14%">性别</td>
					<td width="16%">{{ sex }}</td>
					<td width="14%">部门</td>
					<td>{{ dept }}</td>
				</tr>
				<tr>
					<td align="center">请假类别</td>
					<td colspan="5">{{ type }}</td>
				</tr>
				<tr>
					<td align="center">请假时间</td>
					<td colspan="5">{{ start }}&nbsp;&nbsp;~&nbsp;&nbsp;{{ end }}</td>
				</tr>
				<tr>
					<td align="center">请假事由</td>
					<td colspan="5">{{ reason }}</td>
				</tr>
				<tr>
					<td align="center">此处签字</td>
					<td colspan="2"></td>
					<td align="center">人事盖章</td>
					<td colspan="3"></td>
				</tr>
			</table>

			<p class="desc">备注：员工请假期间一切责任自负，假期结束后应及时返回工作岗位，否则按照旷工处理。</p>
		</div>
		<template #footer>
			<span class="dialog-footer">
				<el-button type="primary" @click="getPdf('#pdfDom')" size="medium">下载请假单</el-button>
				<el-button size="medium" @click="cancel()">取消</el-button>
				
			</span>
		</template>
	</el-dialog>
</template>

<script>
export default {
	data: function() {
		return {
			visible: false,
			htmlTitle: '员工请假单',
			name: null,
			sex: null,
			dept: null,
			reason: null,
			start: null,
			end: null,
			type: null
		};
	},
	methods: {
		init: function (id) {
			let that = this
			that.visible = true;
			that.name = null;
			that.sex = null;
			that.dept = null;
			that.reason = null;
			that.start = null;
			that.end = null;
			that.type = null;
			that.$http('/leave/searchLeaveInfoById', 'POST', {id: id}, true, function (resp) {
				let leaveInfo = resp.leaveInfo
				that.name = leaveInfo.name;
				that.sex = leaveInfo.sex;
				that.dept = leaveInfo.deptName;
				that.reason = leaveInfo.reason;
				if (leaveInfo.type === 1) {
					that.type = '病假';
				} else if (leaveInfo.type === 2) {
					that.type = '事假';
				}
				that.start = leaveInfo.start;
				that.end = leaveInfo.end;
			})
		},
		cancel: function () {
			this.visible = false
		}
	}
};
</script>

<style lang="less" scoped="scoped">
	@import url('leave_pdf.less');
</style>
