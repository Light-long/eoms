<template>
	<el-dialog title="任务详情" :close-on-click-modal="false" v-model="visible"
			   width="500px">
		<el-row :gutter="20">
			<el-col :span="24" :xs="24">
				<el-card class="box-card" shadow="hover">
					<ul class="list-group list-group-striped">
						<li class="list-group-item">
							<div style="line-height: 20px; vertical-align: center">
								<span><el-tag size="small" type="success">任务主题</el-tag></span>
								<div class="pull-right">
									<span>{{taskInfo.theme}}</span>
								</div>
							</div>
						</li>
						<li class="list-group-item">
							<div style="line-height: 20px; vertical-align: center">
								<span><el-tag size="small" type="info">任务描述</el-tag></span>
								<div class="pull-right">
									<span>{{taskInfo.desc}}</span>
								</div>
							</div>
						</li>
						<li class="list-group-item">
							<div style="line-height: 20px; vertical-align: center">
								<span><el-tag size="small" type="primary">发布人</el-tag></span>
								<div class="pull-right">
									<span>{{taskInfo.publisherName}}</span>
								</div>
							</div>
						</li>
						<li class="list-group-item">
							<div style="line-height: 20px; vertical-align: center">
								<span><el-tag size="small" type="info">发布时间</el-tag></span>
								<div class="pull-right">
									<span>{{taskInfo.publishTime}}</span>
								</div>
							</div>
						</li>
                        <li class="list-group-item">
                            <div style="line-height: 20px; vertical-align: center">
                                <span><el-tag size="small" type="primary">执行人</el-tag></span>
                                <div class="pull-right">
                                    <span>{{taskInfo.executorName}}</span>
                                </div>
                            </div>
                        </li>
						<li class="list-group-item">
							<div style="line-height: 20px; vertical-align: center">
								<span><el-tag size="small" type="info">规定时间</el-tag></span>
								<div class="pull-right">
									<span>{{taskInfo.startTime}} ~ {{taskInfo.endTime}}</span>
								</div>
							</div>
						</li>
						<li class="list-group-item">
							<div style="line-height: 40px; vertical-align: center">
								<span><el-tag size="medium" type="warning">进度</el-tag></span>
								<el-progress :percentage="taskInfo.percent" :color="color" style="margin-left: 25px"/>
								<div align="center" v-if="isUpdate">
									<el-button-group>
										<el-button icon="el-icon-minus" size="small" @click="decrease" />
										<el-button icon="el-icon-plus" size="small" @click="increase" />
									</el-button-group>
								</div>
							</div>
						</li>
						<li class="list-group-item">
							<div style="line-height: 20px; vertical-align: center">
								<span><el-tag size="small" type="primary">状态</el-tag></span>
								<div class="pull-right">
									<el-tag type="warning" v-if="taskInfo.taskStatus === '新任务'">{{taskInfo.taskStatus}}</el-tag>
									<el-tag type="info" v-if="taskInfo.taskStatus === '已取消'">{{taskInfo.taskStatus}}</el-tag>
									<el-tag type="primary" v-if="taskInfo.taskStatus === '进行中'">{{taskInfo.taskStatus}}</el-tag>
									<el-tag type="success" v-if="taskInfo.taskStatus === '已完成'">{{taskInfo.taskStatus}}</el-tag>
									<el-tag type="success" v-if="taskInfo.taskStatus === '已评分'">{{taskInfo.taskStatus}}</el-tag>
									<el-tag type="danger" v-if="taskInfo.taskStatus === '已过期'">{{taskInfo.taskStatus}}</el-tag>
								</div>
							</div>
						</li>
						<li class="list-group-item" v-if="taskInfo.rate != null">
							<div style="line-height: 20px; vertical-align: center">
								<span><el-tag size="small" type="info">评分</el-tag></span>
								<div class="pull-right">
									<el-rate
										disabled
										v-model="taskInfo.rate"
										size="small" />
								</div>
							</div>
						</li>
					</ul>
				</el-card>
			</el-col>
		</el-row>
		<template #footer>
			<div class="dialog-footer" v-if="!isUpdate">
				<el-button type="primary" size="small" @click="visible = false">知道了</el-button>
			</div>
			<div class="dialog-footer" v-if="isUpdate">
				<el-button type="common" size="small" @click="visible = false">知道了</el-button>
				<el-button type="primary" size="small" @click="updateTaskDegree()">提交进度</el-button>
			</div>
		</template>
	</el-dialog>
</template>

<script>
	import SvgIcon from "../../components/SvgIcon.vue";
	import dayjs from "dayjs";
export default {
	components: {
		SvgIcon
	},
	data: function() {
		return {
			visible: false,
			taskInfo: {
				id: null,
				theme: null,
				desc: null,
				publisherName: null,
				publishTime: null,
				executorName: null,
				startTime: null,
				endTime: null,
				taskStatus: null,
				percent: null,
				rate: null
			},
			color: [
				{ color: '#f56c6c', percentage: 20 },
				{ color: '#e6a23c', percentage: 40 },
				{ color: '#5cb87a', percentage: 60 },
				{ color: '#1989fa', percentage: 80 },
				{ color: '#6f7ad3', percentage: 100 },
			],
			isUpdate: false
		};
	},
	methods: {
		init: function(id, updatePercent) {
			let that = this;
			that.visible = true;
			that.isUpdate = updatePercent
			// 加载任务详情
			that.$nextTick(() => {
				that.$http('/task/searchTaskInfo', 'POST', {id: id}, true, function (resp) {
					that.taskInfo = resp.taskInfo
					if (that.taskInfo.taskStatus === 0) {
						that.taskInfo.taskStatus = '已过期'
					} else if (that.taskInfo.taskStatus === 1) {
						that.taskInfo.taskStatus = '新任务'
					} else if (that.taskInfo.taskStatus === 2) {
						that.taskInfo.taskStatus = '已取消'
					} else if (that.taskInfo.taskStatus === 3) {
						that.taskInfo.taskStatus = '进行中'
					} else if (that.taskInfo.taskStatus === 4) {
						that.taskInfo.taskStatus = '已完成'
					} else if (that.taskInfo.taskStatus === 5) {
						that.taskInfo.taskStatus = '已评分'
					}
				})
			})
		},
		decrease: function () {
			this.taskInfo.percent -= 20
			if (this.taskInfo.percent < 0) {
				this.taskInfo.percent = 0
			}
		},
		increase: function () {
			this.taskInfo.percent += 20
			if (this.taskInfo.percent > 100) {
				this.taskInfo.percent = 100
			}
		},
		updateTaskDegree: function () {
			let that = this
			let data = {
				id: that.taskInfo.id,
				percent: that.taskInfo.percent
			}
			that.$http('/task/updateTaskDegree', 'POST', data, true, function (resp) {
				if (resp.rows === 1) {
					that.visible = false
					that.$message({
						message: '修改进度成功',
						type: 'success',
						duration: 1200
					});
					that.$emit('refreshDataList')
				} else {
					that.$message({
						message: '操作失败',
						type: 'error',
						duration: 1200
					});
				}
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
