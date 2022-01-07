<template>
	<el-dialog title="会议概要信息" :close-on-click-modal="false" v-model="visible" width="450px">
		<div>
			<div class="banner"></div>
			<el-row class="info">
				<el-col :span="3" class="label">主题：</el-col>
				<el-col :span="21">
					<span class="value">{{ title }}</span>
				</el-col>
			</el-row>
			<el-row class="info">
				<el-col :span="3" class="label">日期：</el-col>
				<el-col :span="9" class="value">{{ date }}</el-col>
				<el-col :span="3" class="label">地点：</el-col>
				<el-col :span="9" class="value">{{ place }}</el-col>
			</el-row>
			<el-row class="info">
				<el-col :span="3" class="label">时间：</el-col>
				<el-col :span="9" class="value">{{ start }} ~ {{ end }}</el-col>
				<el-col :span="3" class="label">状态：</el-col>
				<el-col :span="9" class="value">{{ status }}</el-col>
			</el-row>
			<el-row class="info member" v-if="['待审批', '未开始'].includes(status)">
				<el-col :span="3" class="label">人员：</el-col>
				<el-col :span="21" class="value">
					<ul class="list">
						<li class="item" v-for="one in members">
							<img :src="one.photo" class="photo" />
							<span class="name">{{ one.name }}</span>
						</li>
					</ul>
				</el-col>
			</el-row>
			<el-row class="info member" v-if="['进行中', '已结束'].includes(status)">
				<el-col :span="3" class="label">参会：</el-col>
				<el-col :span="21" class="value">
					<ul class="list">
						<li class="item" v-for="one in present">
							<img :src="one.photo" class="photo" />
							<span class="name">{{ one.name }}</span>
						</li>
					</ul>
				</el-col>
			</el-row>
			<el-row class="info member" v-if="['进行中', '已结束'].includes(status)">
				<el-col :span="3" class="label">缺席：</el-col>
				<el-col :span="21" class="value">
					<ul class="list">
						<li class="item" v-for="one in unpresent">
							<img :src="one.photo" class="photo" />
							<span class="name">{{ one.name }}</span>
						</li>
					</ul>
				</el-col>
			</el-row>
		</div>
		<template #footer>
			<span class="dialog-footer"><el-button size="medium" @click="visible = false">关闭</el-button></span>
		</template>
	</el-dialog>
</template>

<script>
export default {
	data: function() {
		return {
			visible: false,
			title: null,
			date: null,
			place: null,
			start: null,
			end: null,
			members: [],
			present: [],
			unpresent: [],
			status: null
		};
	},
	methods: {
		init: function(id, status) {
			let that = this;
			that.visible = true;
			that.$nextTick(() => {
				let data = {
					id: id,
					status: status
				};
				that.$http('meeting/searchMeetingInfo', 'POST', data, true, function(resp) {
					console.log(resp.data);
					that.title = resp.title;
					that.date = resp.date;
					that.place = resp.place;
					that.start = resp.start;
					that.end = resp.end;

					if (resp.status == 1) {
						that.status = '待审批';
					} else if (resp.status == 3) {
						that.status = '未开始';
					} else if (resp.status == 4) {
						that.status = '进行中';
					} else if (resp.status == 5) {
						that.status = '已结束';
					}
					if (resp.hasOwnProperty('members')) {
						that.members = JSON.parse(resp.members);
					}
					if (resp.hasOwnProperty('present')) {
						that.present = JSON.parse(resp.present);
					}
					if (resp.hasOwnProperty('unpresent')) {
						that.unpresent = JSON.parse(resp.unpresent);
					}
				});
			});
		}
	}
};
</script>

<style lang="less" scoped="scoped">
@import url('offline_meeting-info.less');
</style>
