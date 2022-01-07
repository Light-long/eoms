<template>
	<div>
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
				<el-button size="medium" type="danger" @click="addHandle()">会议申请</el-button>
			</el-form-item>
			<el-form-item class="mold">
				<el-radio-group v-model="dataForm.mold" size="medium" @change="changeHandle">
					<el-radio-button label="全部会议"></el-radio-button>
					<el-radio-button label="我的会议"></el-radio-button>
				</el-radio-group>
			</el-form-item>
		</el-form>
		<div class="gantt" ref="gantt" v-show="mode == 'gantt'">
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
			v-show="mode == 'gantt'"
			@size-change="sizeChangeHandle"
			@current-change="currentChangeHandle"
			:current-page="pageIndex"
			:page-sizes="[10, 20, 50]"
			:page-size="pageSize"
			:total="totalCount"
			layout="total, sizes, prev, pager, next, jumper"
		/>

		<div class="calendar" v-show="mode == 'calendar'">
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
							v-if="
								calendar.map[`${day.date}#${one}`].isCreator == 'true' &&
									[1, 3].includes(calendar.map[`${day.date}#${one}`].status)
							"
							@click.stop="deleteHandle(`${day.date}#${one}`)"
						/>
						{{ calendar.map[`${day.date}#${one}`].title }}
					</div>
				</div>
			</div>
		</div>
		<add v-if="addVisible" ref="add" @refresh="refresh"></add>
		<info v-if="infoVisble" ref="info"></info>
	</div>
</template>

<script>
import SvgIcon from '../components/SvgIcon.vue';
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
			infoVisble: false,
			dataRule: {},
			mode: 'gantt'
		};
	},
	methods: {
		backHandle: function() {
			let that = this;
			that.mode = 'gantt';
		},
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
	},
	created: function() {
		
		let that = this;	
		//加载会议室列表
		that.$http('meeting_room/searchAllMeetingRoom', 'GET', null, true, function(resp) {
			that.roomList = resp.list;
		});

		//加载分页数据
		that.loadDataList();
	}
};
</script>

<style lang="less" scoped="scoped">
@import url('offline_meeting.less');
</style>
