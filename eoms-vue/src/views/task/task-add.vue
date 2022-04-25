<template>
	<el-dialog title="任务发布" :close-on-click-modal="false" v-model="visible" width="500px">
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="80px">
			<el-form-item label="主题" prop="theme">
				<el-input
						type="input"
						v-model="dataForm.theme"
						placeholder="任务主题"
						clearable="clearable"
				/>
			</el-form-item>
			<el-form-item label="描述" prop="desc">
				<el-input
					type="textarea"
					v-model="dataForm.desc"
					placeholder="任务描述"
					:autosize="{ minRows: 4, maxRows: 6 }"
					clearable="clearable"
				/>
			</el-form-item>
			<el-form-item label="开始日期">
				<el-row type="flex" justify="space-between">
					<el-col :span="12">
						<el-form-item prop="startDate" class="form-item">
							<el-date-picker
								v-model="dataForm.startDate"
								value-format="yyyy-MM-dd"
								type="date"
								placeholder="开始日期"
								style="width: 100%;"
								:disabledDate="disabledDate"
								clearable="clearable"
							></el-date-picker>
						</el-form-item>
					</el-col>
					<el-col :span="11">
						<el-form-item prop="startTime" class="form-item">
							<el-time-select
								v-model="dataForm.startTime"
								start='08:00'
								step='00:30'
								end='20:00'
								value-format="HH:mm"
								placeholder="选择时间"
								style="width: 100%;"
							></el-time-select>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form-item>
			<el-form-item label="截止日期">
				<el-row type="flex" justify="space-between">
					<el-col :span="12">
						<el-form-item prop="endDate" class="form-item">
							<el-date-picker
								v-model="dataForm.endDate"
								value-format="yyyy-MM-dd"
								type="date"
								placeholder="截止日期"
								style="width: 100%;"
								:disabledDate="disabledDate"
								clearable="clearable"
							></el-date-picker>
						</el-form-item>
					</el-col>
					<el-col :span="11">
						<el-form-item prop="endTime" class="form-item">
							<el-time-select
								v-model="dataForm.endTime"
								value-format="HH:mm"
								start='08:30'
								step='00:30'
								end='20:30'
								placeholder="选择时间"
								style="width: 100%;"
							></el-time-select>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form-item>
			<el-form-item label="执行人" prop="executorId">
				<el-select
						v-model="dataForm.executorId"
						class="input"
						placeholder="执行人姓名"
						size="small"
						clearable="clearable"
				>
					<el-option v-for="one in userList" :label="one.name" :value="one.id" />
				</el-select>
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button size="medium" @click="visible = false">取消</el-button>
				<el-button type="primary" size="medium" @click="dataFormSubmit">确定</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
import dayjs from 'dayjs';
export default {
	data: function() {
		return {
			visible: false,
			dataForm: {
				theme: null,
				desc: null,
				startDate: null,
				startTime: null,
				endDate: null,
				endTime: null,
				executorId: null
			},
			userList: [],
			// 早于当前的日期不能选择
			disabledDate(date) {
				return dayjs(date.toLocaleDateString()).isBefore(new Date().toLocaleDateString());
			},
			dataRule: {
				theme: [{ required: true, message: '主题不能为空' }],
				desc: [{ required: true, message: '任务描述不能为空' }],
				startDate: [{ required: true, message: '开始日期不能为空' }],
				startTime: [{ required: true, message: '开始时间不能为空' }],
				endDate: [{ required: true, message: '截止日期不能为空' }],
				endTime: [{ required: true, message: '截止时间不能为空' }],
				executorId: [{ required: true, message: '执行人不能为空' }]
			}
		};
	},
	methods: {
		init: function() {
			let that = this;
			that.visible = true;
			that.$nextTick(() => {
				that.$refs['dataForm'].resetFields();
				that.loadUserList()
			});
		},
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
		dataFormSubmit: function () {
			let that = this
			let data = {
				theme: that.dataForm.theme,
				desc: that.dataForm.desc,
				executorId: that.dataForm.executorId,
				startTime: dayjs(that.dataForm.startDate).format('YYYY-MM-DD') + ' ' + that.dataForm.startTime + ':00',
				endTime: dayjs(that.dataForm.endDate).format('YYYY-MM-DD') + ' ' + that.dataForm.endTime + ':00',
			}
			this.$refs['dataForm'].validate(valid => {
				if (valid) {
					that.$http('/task/publishTask', 'POST', data, true, function (resp) {
						if (resp.rows === 1) {
							that.visible = false
							that.$message({
								message: '操作成功',
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
				} else {
					return false
				}
			})
		}
	}
};
</script>

<style lang="less" scoped="scoped">
	.form-item{
		margin-bottom: 0 !important;
	}
</style>
