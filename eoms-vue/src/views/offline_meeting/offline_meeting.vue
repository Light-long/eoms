<template>
	<div>
		<div align="center">
			<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm" class="form">
				<el-form-item prop="name">
					<el-select
							v-model="dataForm.name"
							class="input"
							placeholder="选择会议室"
							size="medium"
							clearable="clearable"
					>
						<el-option v-for="one in roomList" :label="one.name" :value="one.name" />
					</el-select>
				</el-form-item>
				<el-form-item prop="date">
					<el-date-picker
							v-model="dataForm.date"
							type="date"
							placeholder="选择日期"
							class="input"
							size="medium"
					></el-date-picker>
				</el-form-item>
				<el-form-item>
					<el-button size="medium" type="primary" @click="searchHandle()">查询</el-button>
					<el-button size="medium" type="common" @click="reset">重置</el-button>
					<el-button size="medium" type="success" @click="addHandle()">会议申请</el-button>
				</el-form-item>
				<el-form-item class="mold">
					<el-radio-group v-model="dataForm.mold" size="medium" @change="changeHandle">
						<el-radio-button label="全部会议"></el-radio-button>
						<el-radio-button label="我的会议"></el-radio-button>
					</el-radio-group>
				</el-form-item>
			</el-form>
		</div>
		<div class="gantt" ref="gantt" v-show="mode === 'gantt'">
			<div class="row">
				<div class="cell-time"></div>
				<div class="cell-time" v-for="one in time">
					<span class="time">{{ one }}</span>
				</div>
			</div>
			<div class="row" v-for="room in gantt.meetingRoom">
				<div class="cell room">{{ room.name }}</div>
				<div class="cell" v-for="one in time">
					<div
						v-if="room.meeting.hasOwnProperty(one)"
						class="meeting"
						:class="room.meeting[one].split('#')[1]"
						:style="'width:' + room.meeting[one].split('#')[0] * gantt.cellWidth + 'px'"
					></div>
				</div>
			</div>
		</div>
		<el-pagination
			v-show="mode === 'gantt'"
			@size-change="sizeChangeHandle"
			@current-change="currentChangeHandle"
			:current-page="pageIndex"
			:page-sizes="[5, 10, 20]"
			:page-size="pageSize"
			:total="totalCount"
			layout="total, sizes, prev, pager, next, jumper"
		/>

		<div class="calendar" v-show="mode === 'calendar'">
			<div class="row">
				<div class="cell">时间</div>
				<div class="cell" v-for="one in calendar.days">{{ one.date }}（{{ one.day }}）</div>
			</div>
			<div class="row" v-for="(one, index) in time">
				<div class="cell-time" v-if="time[index + 1] != null">{{ one }} ~ {{ time[index + 1] }}</div>
				<div class="cell" v-for="day in calendar.days" v-if="time[index + 1] != null">
					<div
						class="meeting"
						v-if="calendar.map.hasOwnProperty(`${day.date}#${one}`)"
						:style="
							'height:' +
								calendar.map[day.date + '#' + one].time * 31 +
								'px; line-height:' +
								calendar.map[day.date + '#' + one].time * 31 +
								'px'
						"
						:ref="`${day.date}#${one}`"
						@click="
							infoHandle(calendar.map[day.date + '#' + one].id, calendar.map[day.date + '#' + one].status)
						"
					>
						<SvgIcon
							name="close"
							class="icon-svg-close"
							v-if="calendar.map[`${day.date}#${one}`].isCreator === 'true' && [1, 3].includes(calendar.map[`${day.date}#${one}`].status)"
							@click.stop="deleteHandle(`${day.date}#${one}`)"
						/>
						{{ calendar.map[`${day.date}#${one}`].title }}
					</div>
				</div>
			</div>
		</div>
		<add v-if="addVisible" ref="add" @refresh="refresh"></add>
		<info v-if="infoVisible" ref="info"></info>
	</div>
</template>

<script>
import SvgIcon from '../../components/SvgIcon.vue';
import dayjs from 'dayjs';
import Add from './offline_meeting-add.vue';
import Info from './offline_meeting-info.vue';
export default {
	components: { SvgIcon, Add, Info },
	data: function() {
		return {
			time: [
				'08:30',
				'09:00',
				'09:30',
				'10:00',
				'10:30',
				'11:00',
				'11:30',
				'12:00',
				'12:30',
				'13:00',
				'13:30',
				'14:00',
				'14:30',
				'15:00',
				'15:30',
				'16:00',
				'16:30',
				'17:00',
				'17:30',
				'18:00',
				'18:30'
			],
			gantt: {
				meetingRoom: [],
				cellWidth: 0
			},
			calendar: {
				map: {},
				days: []
			},
			roomList: [],
			dataForm: {
				name: null,
				date: null,
				mold: '全部会议'
			},
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			dataListLoading: false,
			addVisible: false,
			infoVisible: false,
			dataRule: {},
			mode: 'gantt'
		};
	},
	created: function() {
		let that = this;
		//加载会议室列表
		that.$http('/meetingRoom/searchAllMeetingRoom', 'GET', null, true, function(resp) {
			that.roomList = resp.list;
		});
		//加载分页数据
		that.loadDataList();
	},
	methods: {
		loadDataList: function() {
			let that = this
			that.dataListLoading = true
			let data = {
				name: that.dataForm.name,
				mold: that.dataForm.mold,
				page: that.pageIndex,
				length: that.pageSize
			}
			if (that.dataForm.date == null || that.dataForm.date === '') {
				data.date = dayjs(new Date()).format('YYYY-MM-DD')
			} else {
				data.date = dayjs(that.dataForm.date).format('YYYY-MM-DD')
			}
			that.$http('/meeting/searchOfflineMeetingByPage', "POST", data, true, function (resp) {
				let page = resp.page
				let temp = []
				for (let room of page.list) {
					let json = {}
					json.name = room.name
					// 一个会议室中的所有会议数据
					json.meeting = {}
					if (room.hasOwnProperty('meeting')) {
						for (let meeting of room.meeting) {
							let color
							if (meeting.status === 1) {
								color = 'yellow'
							} else if (meeting.status === 3) {
								color = 'blue';
							} else if (meeting.status === 4) {
								color = 'pink';
							} else if (meeting.status === 5) {
								color = 'gray';
							}
							json.meeting[meeting.start] = meeting.time + "#" + color
						}
					}
					temp.push(json)
				}
				that.gantt.meetingRoom = temp
				that.totalCount = page.totalCount
				that.dataListLoading = false
			})
		},
		searchHandle: function() {
			let that = this
			// 数据以甘特图展示
			if (that.dataForm.name == null || that.dataForm.name === '') {
				that.$refs['dataForm'].validate(valid => {
					if (valid) {
						that.$refs['dataForm'].clearValidate()
						that.dataForm.name = null
						if (that.pageIndex !== 1) {
							that.pageIndex = 1
						}
						that.loadDataList()
						that.mode = 'gantt'
					} else {
						return false
					}
				})
				// 数据以-周日历展示
			} else {
				that.$refs['dataForm'].validate(valid => {
					if (valid) {
						that.$refs['dataForm'].clearValidate()
						let data = {
							name: that.dataForm.name,
							mold: that.dataForm.mold
						}
						if (that.dataForm.date != null && that.dataForm.date !== '') {
							data.date = dayjs(that.dataForm.date).format("YYYY-MM-DD")
						}
						that.$http("/meeting/searchOfflineMeetingInWeek", "POST", data, true, function (resp) {
							// {
							// 	"07/31#09:00": {title:"测试会议1", date:"2021-07-31", start:"09:00", time:2, ……} ,
							// 	"07/31#15:30": {title:"测试会议2", date:"2021-07-31", start:"15:30", time:2, ……} ,
							// }
							let meetingList = {}
							for (let one of resp.list) {
								meetingList[`${one.date}#${one.start}`] = one
							}
							that.calendar.map = meetingList
							// 周日历表头标题
							that.calendar.days = resp.days
							that.mode = 'calendar'
						})
					} else {
						return false
					}
				})
			}
		},
		// 切换 我的会议|全部会议
		changeHandle: function() {
			this.searchHandle()
		},
		sizeChangeHandle: function(val) {
			this.pageSize = val
			this.pageIndex = 1
			this.loadDataList()
		},
		currentChangeHandle: function(val) {
			this.pageIndex = val
			this.loadDataList()
		},
		backHandle: function() {
			let that = this;
			that.mode = 'gantt';
		},
		addHandle: function() {
			this.addVisible = true
			this.$nextTick(() => {
				this.$refs.add.init();
			});
		},
		refresh: function () {
			this.mold = 'gantt'
			this.$refs['dataForm'].resetFields()
			this.loadDataList()
		},
		infoHandle: function (id, status) {
			this.infoVisible = true
			this.$nextTick(() => {
				this.$refs.info.init(id, status)
			})
		},
		deleteHandle: function (key) {
			let that = this;
			that.$confirm('是否删除该会议?', '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				let json = that.calendar.map[key]
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
		reset: function () {
			this.dataForm = []
			this.dataForm.mold = '全部会议'
			this.searchHandle()
		}
	},
	mounted: function() {
		let that=this
		//根据实际情况设置甘特图每个单元格应该有的宽度
		let rowWidth = that.$refs['gantt'].offsetWidth - 1;
		let cellWidth = rowWidth * 0.042 + 0.01;
		that.gantt.cellWidth = cellWidth;

		//当浏览器窗口尺寸改变的时候，重新设置甘特图单元格的宽度
		window.addEventListener('resize', () => {
			let rowWidth = that.$refs['gantt'].offsetWidth - 1;
			let cellWidth = rowWidth * 0.042 + 0.01;
			that.gantt.cellWidth = cellWidth;
		})
	}
};
</script>

<style lang="less" scoped="scoped">
	@import url('offline_meeting.less');
</style>
