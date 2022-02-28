<template>
	<el-dialog title="请假申请" :close-on-click-modal="false" v-model="visible" width="450px">
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="80px">
			<el-form-item label="请假原因" prop="reason">
				<el-input
					type="textarea"
					v-model="dataForm.reason"
					placeholder="请假原因"
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
			<el-form-item label="请假类型" prop="type">
				<el-radio-group v-model="dataForm.type">
					<el-radio :label="1">病假</el-radio>
					<el-radio :label="2">事假</el-radio>
				</el-radio-group>
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
				reason: null,
				startDate: null,
				startTime: null,
				endDate: null,
				endTime: null,
				type: null
			},
			// 早于当前的日期不能选择
			disabledDate(date) {
				return dayjs(date.toLocaleDateString()).isBefore(new Date().toLocaleDateString());
			},
			dataRule: {
				reason: [{ required: true, message: '请假原因不能为空' }],
				startDate: [{ required: true, message: '开始日期不能为空' }],
				startTime: [{ required: true, message: '开始时间不能为空' }],
				endDate: [{ required: true, message: '截止日期不能为空' }],
				endTime: [{ required: true, message: '截止时间不能为空' }],
				type: [{ required: true, message: '请假类型不能为空' }]
			}
		};
	},
	methods: {
		init: function() {
			let that = this;
			that.visible = true;
			that.$nextTick(() => {
				that.$refs['dataForm'].resetFields();
			});
		},
		dataFormSubmit: function () {
			let that = this
			let data = {
				reason: that.dataForm.reason,
				start: dayjs(that.dataForm.startDate).format('YYYY-MM-DD') + ' ' + that.dataForm.startTime + ':00',
				end: dayjs(that.dataForm.endDate).format('YYYY-MM-DD') + ' ' + that.dataForm.endTime + ':00',
				type: that.dataForm.type
			}
			this.$refs['dataForm'].validate(valid => {
				if (valid) {
					that.$http('/leave/addLeave', 'POST', data, true, function (resp) {
						if (resp.rows === 1) {
							that.visible = false
							that.$message({
								message: '操作成功',
								type: 'success',
								duration: 1200
							});
							setTimeout(function () {
								that.$emit('refreshDataList')
							}, 1000)

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
