<template>
	<div class="app-container">
		<!--查询条件-->
		<div align="center">
			<el-form :inline="true" :model="dataForm" ref="dataForm" class="form" label-width="100px">
				<el-form-item prop="roomName" label="会议室名称">
					<el-select
							v-model="dataForm.roomName"
							class="input"
							placeholder="选择会议室"
							size="small"
							clearable="clearable"
					>
						<el-option v-for="one in roomList" :label="one.name" :value="one.name" />
					</el-select>
				</el-form-item>
				<el-form-item prop="date" label="会议日期" label-width="80px">
					<el-date-picker
							v-model="dataForm.date"
							type="date"
							placeholder="选择日期"
							class="input"
							size="small"
					></el-date-picker>
				</el-form-item>
				<el-form-item>
					<el-button size="small" type="primary" @click="searchHandle()">查询</el-button>
					<el-button size="small" type="common" @click="reset">重置</el-button>
					<el-button size="small" type="success" @click="addHandle()">会议申请</el-button>
				</el-form-item>
				<el-form-item class="mold" v-if="isAuth(['ROOT', 'MEETING:SELECT'])">
					<el-radio-group v-model="dataForm.mold" size="small" @change="changeHandle">
						<el-radio-button label="我的会议"></el-radio-button>
						<el-radio-button label="全部会议"></el-radio-button>
					</el-radio-group>
				</el-form-item>
			</el-form>
		</div>
		<!--内容-->
		<el-row :gutter="20">
			<!--线下会议-->
			<el-col :span="12" :xs="24">
				<div style="font-size: 22px; font-weight: bold; margin-bottom: 10px" align="center" >线下会议</div>
				<div>
					<el-card shadow="none" class="box-card">
						<div style="font-size: 22px; font-weight: bold; margin-bottom: 5px" >{{showDate}}</div>
						<div v-if="offlineMeetingList.length === 0">
							<el-card shadow="none" class="box-card">
								<div align="center">暂无数据</div>
							</el-card>
						</div>
						<div v-if="offlineMeetingList.length !== 0" v-for="offlineMeeting in offlineMeetingList">
							<el-card shadow="hover" class="box-card" style="margin-bottom: 10px">
								<div slot="header" class="clearfix">
									<span style="font-size: 18px; font-weight: bold">{{offlineMeeting.title}}</span>
									<div class="pull-right">
										<el-button
												size="small"
												type="text"
												icon="el-icon-zoom-in"
												@click="infoHandle(offlineMeeting.id, offlineMeeting.status)"
												v-if="dataForm.mold === '我的会议' && offlineMeeting.status !== 2"
										>查看详情</el-button>
										<el-button
												size="small"
												type="text"
												icon="el-icon-delete"
												v-if="offlineMeeting.isCreator === 'true' && [1, 3].includes(offlineMeeting.status)"
												@click="deleteHandle(offlineMeeting)"
										>删除</el-button>
									</div>
								</div>
								<div>
									<ul class="list-group list-group-striped">
										<li class="list-group-item">
											<div style="line-height: 20px; vertical-align: center">
												<SvgIcon name="geren" class="icon-svg-4"></SvgIcon>
												<span>创建人姓名</span>
												<div class="pull-right">{{offlineMeeting.name}}</div>
											</div>
										</li>
										<li class="list-group-item">
											<div style="line-height: 20px; vertical-align: center">
												<SvgIcon name="dangdifill" class="icon-svg-4"></SvgIcon>
												<span>地点</span>
												<div class="pull-right">{{offlineMeeting.place}}会议室</div>
											</div>
										</li>
										<li class="list-group-item">
											<div style="line-height: 20px; vertical-align: center">
												<SvgIcon name="job" class="icon-svg-3"></SvgIcon>
												<span>起止时间</span>
												<div class="pull-right">{{offlineMeeting.start}} ~ {{offlineMeeting.end}}</div>
											</div>
										</li>
										<li class="list-group-item">
											<div style="line-height: 20px; vertical-align: center">
												<SvgIcon name="xingqu" class="icon-svg-4"></SvgIcon>
												<span>状态</span>
												<div class="pull-right">
													<el-tag size="small" type="warning" v-if="offlineMeeting.status=== 1">申请中</el-tag>
													<el-tag size="small" type="danger" v-if="offlineMeeting.status=== 2">审批未通过</el-tag>
													<el-tag size="small" type="success" v-if="offlineMeeting.status=== 3">审批通过,待开始</el-tag>
													<el-tag size="small" type="primary" v-if="offlineMeeting.status=== 4">会议进行中</el-tag>
													<el-tag size="small" type="info" v-if="offlineMeeting.status=== 5">会议结束</el-tag>
												</div>
											</div>
										</li>
										<li class="list-group-item">
											<div style="line-height: 20px; vertical-align: center">
												<SvgIcon name="pinglun" class="icon-svg-3"></SvgIcon>
												<span>会议内容</span>
												<div class="pull-right">{{offlineMeeting.desc}}</div>
											</div>
										</li>
									</ul>
								</div>
							</el-card>
						</div>
					</el-card>
				</div>
			</el-col>
			<el-col :span="12" :xs="24">

			</el-col>
		</el-row>
		<add v-if="addVisible" ref="add" @refresh="refresh"></add>
		<info v-if="infoVisible" ref="info"></info>
	</div>
</template>

<script>
	import SvgIcon from '../../components/SvgIcon.vue';
	import dayjs from 'dayjs';
	import Add from './meeting-add.vue';
	import Info from './offline_meeting-info.vue';
export default {
	components: {SvgIcon, Info, Add},
	data: function() {
		return {
			roomList: [],
			dataForm: {
				roomName: null,
				date: null,
				mold: '我的会议'
			},
			offlineMeetingList: [],
			onlineMeetingList: [],
			showDate: null,
			dataListLoading: false,
			addVisible: false,
			infoVisible: false,
		};
	},
	created: function() {
		let that = this;
		//加载会议室列表
		that.$http('/meetingRoom/searchAllMeetingRoom', 'GET', null, true, function(resp) {
			that.roomList = resp.list;
		});
		//加载线下会议数据
		that.loadOfflineMeetingList()
		// TODO 加载线上会议数据
	},
	methods: {
		// 加载线下会议数据
		loadOfflineMeetingList: function() {
			let that = this
			that.dataListLoading = true
			let data = {
				roomName: that.dataForm.roomName,
				mold: that.dataForm.mold
			}
			// 如果没有选择日期，默认是当天的会议数据
			if (that.dataForm.date === null || that.dataForm.date === '') {
				data.date = dayjs(new Date()).format('YYYY-MM-DD')
			} else {
				data.date = dayjs(that.dataForm.date).format('YYYY-MM-DD')
			}
			that.$http('/meeting/searchOfflineMeetingList', 'POST', data, true, function (resp) {
				that.offlineMeetingList = resp.list
				if (resp.list.length > 0) {
					that.showDate = resp.list[0].date
				}
				if (that.dataForm.date != null && that.dataForm.date !== ''){
					that.showDate = dayjs(that.dataForm.date).format('YYYY-MM-DD')
				} else {
					that.showDate = dayjs(new Date()).format('YYYY-MM-DD')
				}
			})
		},
		// 条件查询
		searchHandle: function() {
			this.$refs["dataForm"].validate( valid => {
				if (valid) {
					this.$refs["dataForm"].clearValidate()
					this.loadOfflineMeetingList()
				} else {
					return false
				}
			})
		},
		// 切换 我的会议|全部会议
		changeHandle: function() {
			this.searchHandle()
		},
		// 添加会议
		addHandle: function() {
			this.addVisible = true
			this.$nextTick(() => {
				this.$refs.add.init();
			});
		},
		// 查看线下会议详细信息
		infoHandle: function (id, status) {
			this.infoVisible = true
			this.$nextTick(() => {
				this.$refs.info.init(id, status)
			})
		},
		// 删除会议信息
		deleteHandle: function (offlineMeeting) {
			let that = this;
			that.$confirm('是否删除该会议?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				let json = offlineMeeting
				let data = {
					id: json.id,
					uuid: json.uuid,
					instanceId: json.instanceId,
					reason: "删除会议申请"
				}
				that.$http("/meeting/deleteMeetingApplication", "POST", data, true, function (resp) {
					if (resp.rows === 1) {
						that.$message({
							message: '删除成功',
							type: 'success',
							duration: 1200
						});
						that.searchHandle()
					} else {
						that.$message({
							message: '删除失败',
							type: 'error',
							duration: 1200
						});
					}
				})
			})
		},
		// 重置表单
		reset: function () {
			this.dataForm = []
			this.dataForm.mold = '我的会议'
			this.searchHandle()
		},
		refresh: function () {
			this.loadOfflineMeetingList()
			// TODO 刷新线上会议数据
		}
	}
};
</script>

<style lang="less" scoped="scoped">
	@import url('meeting.less');
</style>
