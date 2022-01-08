<template>
	<el-dialog
		:title="!dataForm.id ? '新增' : '修改'"
		v-if="isAuth(['ROOT', 'USER:INSERT', 'USER:UPDATE'])"
		:close-on-click-modal="false"
		v-model="visible"
		width="450px"
	>
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="80px">
			<el-form-item label="用户名" prop="username">
				<el-input v-model="dataForm.username" size="medium" clearable />
			</el-form-item>
			<el-form-item label="密码" prop="password">
				<el-input type="password" v-model="dataForm.password" size="medium" clearable />
			</el-form-item>
			<el-form-item label="姓名" prop="name">
				<el-input v-model="dataForm.name" size="medium" clearable />
			</el-form-item>
			<el-form-item label="性别" prop="sex">
				<el-select v-model="dataForm.sex" size="medium" style="width: 100%;" clearable>
					<el-option label="男" value="男"></el-option>
					<el-option label="女" value="女"></el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="电话" prop="tel">
				<el-input v-model="dataForm.tel" size="medium" clearable />
			</el-form-item>
			<el-form-item label="邮箱" prop="email">
				<el-input v-model="dataForm.email" size="medium" clearable />
			</el-form-item>
			<el-form-item label="入职日期" prop="hiredate">
				<el-date-picker
					v-model="dataForm.hiredate"
					type="date"
					placeholder="选择日期"
					size="medium"
					:editable="false"
					format="YYYY-MM-DD"
					value-format="YYYY-MM-DD"
					style="width: 100%;"
				/>
			</el-form-item>
			<el-form-item label="角色" prop="role">
				<el-select
					v-model="dataForm.role"
					size="medium"
					placeholder="选择角色"
					style="width: 100%;"
					multiple
					clearable
				>
					<el-option
						v-for="one in roleList"
						:key="one.id"
						:label="one.roleName"
						:value="one.id"
						:disabled="one.roleName == '超级管理员'"
					></el-option>
				</el-select>
			</el-form-item>
			<el-form-item label="部门" prop="deptId">
				<el-select
					v-model="dataForm.deptId"
					size="medium"
					placeholder="选择部门"
					style="width: 100%;"
					clearable
				>
					<el-option v-for="one in deptList" :key="one.id" :label="one.deptName" :value="one.id" />
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
				id: null,
				username: null,
				password: null,
				name: null,
				sex: null,
				tel: null,
				email: null,
				hiredate: new Date(),
				role: null,
				deptId: null,
				status: 1
			},
			roleList: [],
			deptList: [],
			dataRule: {
				username: [{ required: true, pattern: '^[a-zA-Z0-9]{5,20}$', message: '用户名格式错误' }],
				password: [{ required: true, pattern: '^[a-zA-Z0-9]{5,20}$', message: '密码格式错误' }],
				name: [{ required: true, pattern: '^[\u4e00-\u9fa5]{2,10}$', message: '姓名格式错误' }],
				sex: [{ required: true, message: '性别不能为空' }],
				tel: [{ required: false, pattern: '^1\\d{10}$', message: '电话格式错误' }],
				email: [
					{
						required: false,
						pattern: '^([a-zA-Z]|[0-9])(\\w|\\-)+@[a-zA-Z0-9]+\\.([a-zA-Z]{2,4})$',
						message: '邮箱格式错误'
					}
				],
				hiredate: [{ required: true, trigger: 'blur', message: '入职日期不能为空' }],
				role: [{ required: true, message: '角色不能为空' }],
				deptId: [{ required: true, message: '部门不能为空' }],
				status: [{ required: true, message: '状态不能为空' }]
			}
		};
	},
	methods: {
		init: function(id) {
			let that = this;
			that.dataForm.id = id || 0;
			that.visible = true;
			that.$nextTick(() => {
				that.$refs['dataForm'].resetFields();
				that.$http('/role/searchAllRoles', 'GET', null, true, function(resp) {
					that.roleList = resp.roles
				});
				that.$http('/dept/searchAllDept', 'GET', null, true, function(resp) {
					that.deptList = resp.deptList
				});
				// 修改，要先查出员工信息
				if (that.dataForm.id) {
					that.$http('/user/searchUserById', 'POST', { userId: id }, true, function(resp) {
						let user = resp.user
						that.dataForm.username = user.username;
						that.dataForm.password = user.password;
						that.dataForm.name = user.name;
						that.dataForm.sex = user.sex;
						that.dataForm.tel = user.tel;
						that.dataForm.email = user.email;
						that.dataForm.hiredate = user.hiredate;
						that.dataForm.role = JSON.parse(user.role);
						that.dataForm.deptId = user.deptId;
						that.dataForm.status = user.status;
					});
				}
			});
		},
		dataFormSubmit: function () {
			let that = this
			that.visible = true
			that.$refs['dataForm'].validate((valid) => {
				if (valid) {
					let data = {
						userId: that.dataForm.id,
						username: that.dataForm.username,
						password: that.dataForm.password,
						name: that.dataForm.name,
						sex: that.dataForm.sex,
						tel: that.dataForm.tel,
						email: that.dataForm.email,
						hiredate: dayjs(that.dataForm.hiredate).format('YYYY-MM-DD'),
						role: that.dataForm.role,
						deptId: that.dataForm.deptId,
						status: that.dataForm.status
					}
					that.$http(`/user/${!that.dataForm.id ? 'addUser':'update'}`, "POST", data, true, function (resp) {
						if (resp.rows == 1) {
							that.$message({
								message: '操作成功',
								type: 'success',
								duration: 1200
							});
							that.visible = false
							that.$emit('refreshDataList');
						} else {
							that.$message({
								message: '操作失败',
								type: 'error',
								duration: 1200
							});
						}
					})
				}
			})
		}
	}
};
</script>

<style lang="less" scoped="scoped">
</style>
