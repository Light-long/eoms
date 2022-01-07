<template>
	<el-dialog title="提示" v-model="visible" width="25%">
		<el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="80px">
			<el-form-item label="新密码" prop="newPassword">
				<el-input type="password" v-model="dataForm.newPassword" size="medium" clearable />
			</el-form-item>
			<el-form-item label="确认密码" prop="confirmPassword">
				<el-input type="password" v-model="dataForm.confirmPassword" size="medium" clearable />
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
export default {
	data() {
		const validateConfirmPassword = (rule, value, callback) => {
			if (value != this.dataForm.newPassword) {
				callback(new Error('两次输入的密码不一致'));
			} else {
				callback();
			}
		};

		return {
			visible: false,
			dataForm: {
				newPassword: '',
				confirmPassword: ''
			},
			dataRule: {
				newPassword: [{ required: true, pattern: '^[a-zA-Z0-9]{5,20}$', message: '密码格式错误' }],
				confirmPassword: [
					{ required: true, pattern: '^[a-zA-Z0-9]{5,20}$', message: '密码格式错误' },
					{ validator: validateConfirmPassword, trigger: 'blur' }
				]
			}
		};
	},
	methods: {
		init: function () {
			//显示弹窗
			this.visible = true
			//因为清空表单控件是异步的，所以把清空表单控件放在下次DOM更新循环中
			this.$nextTick(() => {
				this.$refs['dataForm'].resetFields();
			});
		},
		dataFormSubmit: function () {
			let that = this
			this.$refs['dataForm'].validate(valid => {
				if (valid) {
					let data = {password: that.dataForm.newPassword}
					that.$http("/user/updatePassword", "POST", data, true, function (resp) {
						if (resp.rows === 1) {
							that.$message({
								message: '密码修改成功',
								type: 'success',
								duration: 1200,
							});
							that.visible = false;
						} else {
							that.$message({
								message: '密码修改失败',
								type: 'error',
								duration: 1200,
							});
						}
					})
				}
			})
		}
	}
};
</script>

<style></style>
