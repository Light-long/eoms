<template>
	<div class="app-container">
		<div align="center">
			<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="100px">
				<el-form-item prop="name" label="会议室名称">
					<el-input
							v-model="dataForm.name"
							placeholder="会议室名称"
							size="small"
							class="input"
							clearable="clearable"
							@keyup.enter.native="searchHandle"
					/>
				</el-form-item>
				<el-form-item label="条件" label-width="60px">
					<el-select v-model="dataForm.status" class="input" placeholder="状态" size="small">
						<el-option label="正常" value="1" />
						<el-option label="停用" value="0" />
					</el-select>
				</el-form-item>
				<el-form-item>
					<el-button size="small" type="primary" @click="searchHandle()">查询</el-button>
					<el-button size="small" type="common" @click="resetForm()">重置</el-button>
					<el-button
							size="small"
							type="success"
							v-if="isAuth(['ROOT', 'MEETING_ROOM:INSERT'])"
							@click="addHandle()"
					>
						新增
					</el-button>
				</el-form-item>
			</el-form>
		</div>

		<!--内容-->
		<el-row :gutter="20">
			<el-col :span="11" :xs="24" style="margin-left: 30px">
				<div v-for="(meetingRoom, index) in dataList">
					<el-card v-if="index % 2 !== 1" class="box-card" style="margin-bottom: 10px">
						<div slot="header" class="clearfix">
							<span style="font-size: 18px; font-weight: bold">{{meetingRoom.name}}</span>
							<div class="pull-right">
								<el-button
										size="medium"
										type="text"
										icon="el-icon-edit"
										v-if="isAuth(['ROOT', 'MEETING_ROOM:UPDATE'])"
										@click="updateHandle(meetingRoom.id)"
								>修改</el-button>
								<el-button
										size="medium"
										type="text"
										icon="el-icon-delete"
										v-if="isAuth(['ROOT', 'MEETING_ROOM:DELETE'])"
										@click="deleteHandle(meetingRoom.id)"
										:disabled="meetingRoom.meetings > 0"
								>删除</el-button>
							</div>
						</div>
						<div>
							<ul class="list-group list-group-striped">
								<li class="list-group-item">
									<div style="line-height: 20px; vertical-align: center">
										<SvgIcon name="company_fill" class="icon-svg-3"></SvgIcon>
										<span>会议室名称</span>
										<div class="pull-right">{{meetingRoom.name}}</div>
									</div>
								</li>
								<li class="list-group-item">
									<div style="line-height: 20px; vertical-align: center">
										<SvgIcon name="geren" class="icon-svg-4"></SvgIcon>
										<span>人数上限</span>
										<div class="pull-right">{{meetingRoom.max}} 人</div>
									</div>
								</li>
								<li class="list-group-item">
									<div style="line-height: 20px; vertical-align: center">
										<SvgIcon name="pinglun" class="icon-svg-4"></SvgIcon>
										<span>关联会议个数</span>
										<div class="pull-right">{{meetingRoom.meetings}} 个</div>
									</div>
								</li>
								<li class="list-group-item">
									<div style="line-height: 20px; vertical-align: center">
										<SvgIcon name="xingqu" class="icon-svg-3"></SvgIcon>
										<span>状态</span>
										<div class="pull-right">
											<el-tag type="primary" v-if="meetingRoom.status=== '正常'">{{meetingRoom.status}}</el-tag>
											<el-tag type="danger" v-if="meetingRoom.status=== '停用'">{{meetingRoom.status}}</el-tag>
										</div>
									</div>
								</li>
								<li class="list-group-item">
									<div style="line-height: 20px; vertical-align: center">
										<SvgIcon name="tixing" class="icon-svg-3"></SvgIcon>
										<span>备注</span>
										<div class="pull-right">{{meetingRoom.desc}}</div>
									</div>
								</li>
							</ul>
						</div>
					</el-card>
				</div>
			</el-col>
			<el-col :span="11" :xs="24" style="margin-left: 30px">
				<div v-for="(meetingRoom, index) in dataList">
					<el-card v-if="index % 2 === 1" class="box-card" style="margin-bottom: 10px">
						<div slot="header" class="clearfix">
							<span style="font-size: 18px; font-weight: bold">{{meetingRoom.name}}</span>
							<div class="pull-right">
								<el-button
										size="medium"
										type="text"
										icon="el-icon-edit"
										v-if="isAuth(['ROOT', 'MEETING_ROOM:UPDATE'])"
										@click="updateHandle(meetingRoom.id)"
								>修改</el-button>
								<el-button
										size="medium"
										type="text"
										icon="el-icon-delete"
										v-if="isAuth(['ROOT', 'MEETING_ROOM:DELETE'])"
										@click="deleteHandle(meetingRoom.id)"
										:disabled="meetingRoom.meetings > 0"
								>删除</el-button>
							</div>
						</div>
						<div>
							<ul class="list-group list-group-striped">
								<li class="list-group-item">
									<div style="line-height: 20px; vertical-align: center">
										<SvgIcon name="company_fill" class="icon-svg-3"></SvgIcon>
										<span>会议室名称</span>
										<div class="pull-right">{{meetingRoom.name}}</div>
									</div>
								</li>
								<li class="list-group-item">
									<div style="line-height: 20px; vertical-align: center">
										<SvgIcon name="geren" class="icon-svg-4"></SvgIcon>
										<span>人数上限</span>
										<div class="pull-right">{{meetingRoom.max}} 人</div>
									</div>
								</li>
								<li class="list-group-item">
									<div style="line-height: 20px; vertical-align: center">
										<SvgIcon name="pinglun" class="icon-svg-4"></SvgIcon>
										<span>关联会议个数</span>
										<div class="pull-right">{{meetingRoom.meetings}} 个</div>
									</div>
								</li>
								<li class="list-group-item">
									<div style="line-height: 20px; vertical-align: center">
										<SvgIcon name="xingqu" class="icon-svg-3"></SvgIcon>
										<span>状态</span>
										<div class="pull-right">
											<el-tag type="primary" v-if="meetingRoom.status=== '正常'">{{meetingRoom.status}}</el-tag>
											<el-tag type="danger" v-if="meetingRoom.status=== '停用'">{{meetingRoom.status}}</el-tag>
										</div>
									</div>
								</li>
								<li class="list-group-item">
									<div style="line-height: 20px; vertical-align: center">
										<SvgIcon name="tixing" class="icon-svg-3"></SvgIcon>
										<span>备注</span>
										<div class="pull-right">{{meetingRoom.desc}}</div>
									</div>
								</li>
							</ul>
						</div>
					</el-card>
				</div>
			</el-col>
		</el-row>
		<add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="loadDataList"></add-or-update>
	</div>
</template>

<script>
import AddOrUpdate from './meeting_room-add-or-update.vue';
import SvgIcon from "../../components/SvgIcon.vue";
export default {
	components: {
		AddOrUpdate,
		SvgIcon
	},
	data: function() {
		return {
			dataForm: {
				name: null,
				status: null
			},
			dataList: [],
			dataListLoading: false,
			dataListSelections: [],
			addOrUpdateVisible: false,
			dataRule: {
				name: [{ required: false, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{1,20}$', message: '会议室名称格式错误' }]
			}
		};
	},
	created: function() {
		this.loadDataList()
	},
	methods: {
		selectionChangeHandle: function(val) {
			this.dataListSelections = val;
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
		loadDataList: function () {
			let that = this
			that.dataListLoading = true
			let data = {
				name: that.dataForm.name,
				status: that.dataForm.status
			}
			that.$http("/meetingRoom/searchMeetingRoomByPage", "POST", data, true, function (resp) {
				let list = resp.list
				for (let one of list) {
					if (one.status === 1) {
						one.status = '正常'
					} else {
						one.status = '停用'
					}
				}
				that.dataList = list
				that.dataListLoading = false
			})
		},
		searchHandle: function () {
			this.$refs["dataForm"].validate( valid => {
				if (valid) {
					this.$refs["dataForm"].clearValidate()
					if (this.dataForm.name === "") {
						this.dataForm.name = null
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
		resetForm: function () {
			this.dataForm = []
			this.loadDataList()
		},
		addHandle: function () {
			this.addOrUpdateVisible = true
			this.$nextTick(() => {
				this.$refs.addOrUpdate.init();
			});
		},
		updateHandle: function(id) {
			this.addOrUpdateVisible = true;
			this.$nextTick(() => {
				this.$refs.addOrUpdate.init(id);
			});
		},
		deleteHandle: function (id) {
			let that = this
			let ids = id ? [id] : that.dataListSelections.map(item => item.id)
			if (ids.length === 0) {
				that.$message({
					message: '没有选中记录',
					type: 'warning',
					duration: 1200
				});
			} else {
				that.$confirm(`确定要删除选中的记录？`, '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then( () => {
					that.$http("/meetingRoom/deleteMeetingRoomByIds", "POST", {ids: ids}, true, function (resp) {
						if (resp.rows > 0) {
							that.$message({
								message: '操作成功',
								type: 'success',
								duration: 1200
							});
							that.loadDataList();
						} else {
							that.$message({
								message: '未能删除记录',
								type: 'warning',
								duration: 1200
							});
						}
					})
				})
			}
		}
	},

};
</script>

<style lang="scss">
	@import "src/assets/scss/ruoyi";

	.icon-svg-3 {
		width: 1.5em;
		height: 1.5em;
		fill: currentColor;
		overflow: hidden;
		margin-right: 5px;
	}

	.icon-svg-4 {
		width: 1.3em;
		height: 1.3em;
		fill: currentColor;
		overflow: hidden;
		margin-right: 5px;
	}
</style>
