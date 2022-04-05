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
				<el-card class="box-card">
					<!--echarts-->
					<div class="chart" id="chart" style="height: 310px; width: 310px"></div>
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
	import $ from 'jquery';
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
					// 生成echarts
					let option = {
						title: {
							show: false,
							text: '暂无数据',
							left: 'center',
							top: 'center',
							textStyle: {
								color: 'pink',
								fontSize: 16,
								fontWeight: 400
							}
						},
						tooltip: {
							trigger: 'item'
						},
						legend: {
							top: '5%',
							left: 'center'
						},
						series: [
							{
								name: '当月考勤统计图',
								type: 'pie',
								radius: ['40%', '70%'],
								avoidLabelOverlap: false,
								label: {
									show: false,
									position: 'center'
								},
								emphasis: {
									label: {
										show: true,
										fontSize: '30',
										fontWeight: 'bold'
									}
								},
								labelLine: {
									show: false
								},
								data: [
									{ value: that.attendanceInfo.successSignIn, name: '正常签到' },
									{ value: that.attendanceInfo.late, name: '迟到' },
									{ value: that.attendanceInfo.leaveEarly, name: '早退' },
									{ value: that.attendanceInfo.successSignOut, name: '正常签退' },
									{ value: that.attendanceInfo.workOverTime, name: '加班' },
									{ value: that.attendanceInfo.absence, name: '缺勤' }
								]
							}
						]
					};
					let myChart;
					if (that.attendanceInfo.length === 0) {
						option.title.show = true;
						myChart = that.$echarts.init($('#chart')[0]);
						myChart.setOption(option);
					} else {
						option.title.show = true;
						option.title.text = ''
						myChart = that.$echarts.init($('#chart')[0]);
						myChart.setOption(option);
					}
				})
			})
		}
	}
};
</script>

<style lang="less" scoped="scoped">
	.chart{
		background-color: #fff;
		height: 300px;
	}
</style>
