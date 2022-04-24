<template>
	<div class="app-container">
		<div align="center">
			<el-form :model="dataForm" ref="dataForm" :inline="true">
				<el-form-item label="姓名">
					<el-select
							v-model="dataForm.userId"
							class="input"
							placeholder="姓名"
							size="small"
							clearable="clearable"
					>
						<el-option v-for="one in userList" :label="one.name" :value="one.id" />
					</el-select>
				</el-form-item>
				<el-form-item label="月份">
					<el-date-picker
							v-model="dataForm.month"
							type="month"
							size="small"
							placeholder="选择一个月份"
					/>
				</el-form-item>
				<el-form-item style="margin-left: 20px">
					<el-button type="primary" icon="el-icon-search" size="mini" @click="searchHandle">搜索</el-button>
					<el-button icon="el-icon-refresh" size="mini" @click="resetForm">重置</el-button>
				</el-form-item>
			</el-form>
		</div>
		<div>
			<el-row :gutter="20">
				<el-col :span="12" :xs="24">
					<el-card class="box-card">
						<div align="center" v-if="user != null" style="margin-bottom: 20px">
							<span style="font-weight: bold;font-size: 18px">{{user.name}}-{{yMonth}} 月考勤记录</span>
						</div>
						<ul class="list-group list-group-striped">
							<li class="list-group-item">
								<div style="line-height: 20px; vertical-align: center">
									<span><el-tag size="large" type="success">正常签到天数</el-tag></span>
									<div class="pull-right">
										<el-tag size="large" type="info">{{attendanceInfo.successSignIn}}天</el-tag>
									</div>
								</div>
							</li>
							<li class="list-group-item">
								<div style="line-height: 20px; vertical-align: center">
									<span><el-tag size="large" type="warning">迟到天数</el-tag></span>
									<div class="pull-right">
										<el-tag size="large" type="info">{{attendanceInfo.late}}天</el-tag>
									</div>
								</div>
							</li><li class="list-group-item">
							<div style="line-height: 20px; vertical-align: center">
								<span><el-tag size="large" type="success">正常签退天数</el-tag></span>
								<div class="pull-right">
									<el-tag size="large" type="info">{{attendanceInfo.successSignOut}}天</el-tag>
								</div>
							</div>
						</li><li class="list-group-item">
							<div style="line-height: 20px; vertical-align: center">
								<span><el-tag size="large" type="warning">早退天数</el-tag></span>
								<div class="pull-right">
									<el-tag size="large" type="info">{{attendanceInfo.leaveEarly}}天</el-tag>
								</div>
							</div>
						</li><li class="list-group-item">
							<div style="line-height: 20px; vertical-align: center">
								<span><el-tag size="large" type="primary">加班天数</el-tag></span>
								<div class="pull-right">
									<el-tag size="large" type="info">{{attendanceInfo.workOverTime}}天</el-tag>
								</div>
							</div>
						</li>
							<li class="list-group-item">
								<div style="line-height: 20px; vertical-align: center">
									<span><el-tag size="large" type="danger">缺勤天数</el-tag></span>
									<div class="pull-right">
										<el-tag size="large" type="info">{{attendanceInfo.absence}}天</el-tag>
									</div>
								</div>
							</li>
						</ul>
					</el-card>
				</el-col>
				<el-col :span="12" :xs="24">
					<el-card class="box-card">
						<!--echarts-->
						<div align="center">
							<div class="chart" id="chart" style="height: 400px; width: 400px"></div>
						</div>
					</el-card>
				</el-col>
			</el-row>
		</div>
	</div>
</template>

<script>
	import $ from 'jquery';
export default {
	data() {
		return {
			dataForm: {
				userId: null,
				month: null
			},
			dataListLoading: false,
			userList: [],
			attendanceInfo: {
				successSignIn: 0,
				late: 0,
				successSignOut: 0,
				leaveEarly: 0,
				workOverTime: 0,
				absence: 0
			},
			user: null,
			yMonth: null
		};
	},
	created: function() {
		this.loadUserList()
		this.loadEcharts()
	},
	methods: {
		// 加载用户名
		loadUserList: function() {
			let that = this
			if (this.isAuth(['ROOT'])) {
				// 查询所有用户
				that.$http('/user/searchAllUser', 'GET', null, true, function(resp) {
					that.userList = resp.list
				});
			} else {
				// 查询该部门用户
				that.$http('/user/searchUserByDeptId', 'GET', null, true, function(resp) {
					that.userList = resp.list
				});
			}
		},
		loadEcharts: function () {
			let that = this
			let data = {
				userId: that.dataForm.userId
			}
			if (that.dataForm.month != null) {
				data.month = dayjs(that.dataForm.month).format('YYYY-MM')
			} else {
				data.month = dayjs(new Date()).format('YYYY-MM')
			}
			console.log(data)
			that.$http('/attendance/searchAttendanceStatistic', 'POST', data, true, function (resp) {
				if (resp.attendanceInfo != null) {
					that.attendanceInfo = resp.attendanceInfo
					that.user = resp.user
					that.yMonth = resp.month
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
				} else {
					that.$message({
						message: resp.message,
						type: 'warning',
						duration: 1200
					});
				}
			})
		},
		searchHandle: function () {
			let that = this
			that.loadEcharts()
		},
		resetForm: function () {
			this.dataForm = []
			this.loadEcharts()
		}
	}
};
</script>

<style lang="less" scoped="scoped"></style>
