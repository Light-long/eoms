<template>
	<el-dialog title="本月考勤统计" :close-on-click-modal="false" v-model="visible" width="800px">
		<el-row :gutter="20">
			<el-col :span="12" :xs="24">
				<el-card class="box-card">
					<ul class="list-group list-group-striped">
						<li class="list-group-item">
							<div style="line-height: 20px; vertical-align: center">
								<span><el-tag size="small" type="success">正常签到天数</el-tag></span>
								<div class="pull-right">
									<el-tag size="small" type="info">{{attendanceInfo.successSignIn}}天</el-tag>
								</div>
							</div>
						</li>
						<li class="list-group-item">
							<div style="line-height: 20px; vertical-align: center">
								<span><el-tag size="small" type="warning">迟到天数</el-tag></span>
								<div class="pull-right">
									<el-tag size="small" type="info">{{attendanceInfo.late}}天</el-tag>
								</div>
							</div>
						</li><li class="list-group-item">
						<div style="line-height: 20px; vertical-align: center">
							<span><el-tag size="small" type="success">正常签退天数</el-tag></span>
							<div class="pull-right">
								<el-tag size="small" type="info">{{attendanceInfo.successSignOut}}天</el-tag>
							</div>
						</div>
					</li><li class="list-group-item">
						<div style="line-height: 20px; vertical-align: center">
							<span><el-tag size="small" type="warning">早退天数</el-tag></span>
							<div class="pull-right">
								<el-tag size="small" type="info">{{attendanceInfo.leaveEarly}}天</el-tag>
							</div>
						</div>
					</li><li class="list-group-item">
						<div style="line-height: 20px; vertical-align: center">
							<span><el-tag size="small" type="primary">加班天数</el-tag></span>
							<div class="pull-right">
								<el-tag size="small" type="info">{{attendanceInfo.workOverTime}}天</el-tag>
							</div>
						</div>
					</li>
					<li class="list-group-item">
						<div style="line-height: 20px; vertical-align: center">
							<span><el-tag size="small" type="danger">缺勤天数</el-tag></span>
							<div class="pull-right">
								<el-tag size="small" type="info">{{attendanceInfo.absence}}天</el-tag>
							</div>
						</div>
					</li>
					</ul>
				</el-card>
			</el-col>
			<el-col :span="12" :xs="24">
				<!--echarts-->
				<el-card class="box-card">
				</el-card>
			</el-col>
		</el-row>
		<template #footer>
			<span class="dialog-footer">
				<el-button type="primary" size="small" @click="visible = false">知道了</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
	import SvgIcon from "../../components/SvgIcon.vue";
export default {
	components: {
		SvgIcon
	},
	data: function() {
		return {
			visible: false,
			attendanceInfo: {
				successSignIn: 0,
				late: 0,
				successSignOut: 0,
				leaveEarly: 0,
				workOverTime: 0,
				absence: 0
			}
		};
	},
	methods: {
		init: function() {
			let that = this;
			that.visible = true;
			// 加载签到数据
			that.$nextTick(() => {
				that.$http('/attendance/searchAttendanceInMonth', 'GET', null, true, function (resp) {
					that.attendanceInfo = resp.attendanceInfo
				})
			})
		}
	}
};
</script>

<style lang="less" scoped="scoped">
</style>
