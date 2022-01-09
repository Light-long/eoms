<template>
	<el-dialog
		:title="!dataForm.id ? '新增' : '修改'"
		v-if="isAuth(['ROOT', 'ROLE:INSERT', 'ROLE:UPDATE'])"
		:close-on-click-modal="false"
		v-model="visible"
		custom-class="dialog"
		width="692px"
	>
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="60px">
			<el-form-item label="角色" prop="roleName">
				<el-input v-model="dataForm.roleName" size="medium" style="width:50%" :readonly="systemic" clearable />
				<span class="note">（ 角色名称必须是2~10个字符之间 ）</span>
			</el-form-item>
			<el-form-item label="备注" prop="desc">
				<el-input v-model="dataForm.desc" style="width:50%" size="medium" maxlength="20" clearable />
				<span class="note">（ 备注信息必须是20个字符以内 ）</span>
			</el-form-item>
			<el-form-item label="权限" prop="permissions">
				<el-transfer
					v-model="dataForm.permissions"
					:data="permissionList"
					size="medium"
					:titles="['权限列表', '具备权限']"
					filterable
					filter-placeholder="请输入权限"
				/>
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
	data: function() {
		return {
			visible: false,
			systemic: true,
			dataForm: {
				id: null,
				roleName: null,
				// 用户选中权限
				permissions: [],
				desc: null,
				changed: false
			},
			// 所有权限
			permissionList: [],
			// 该用户已有权限
			oldPermissions: [],
			dataRule: {
				roleName: [
					{ required: true, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{2,10}$', message: '角色名称格式错误' }
				],
				permissions: [
					{ required: true, trigger: 'blur', message: '角色没有关联权限' },
					{ required: false, trigger: 'change', message: '角色没有关联权限' }
				]
			}
		};
	},

	methods: {
		init: function(id, systemic) {
			let that = this;
			that.dataForm.id = id || 0;
			that.systemic = systemic;
			that.visible = true;
			that.$nextTick(() => {
				that.$refs['dataForm'].resetFields();
				// 内置用户的默认权限
				let defaultPermissions = [];
				// 更新是回显角色信息
				if (id) {
					that.$http('/role/searchRoleById', 'POST', { id: id }, true, function(resp) {
						let role = resp.role
						that.dataForm.roleName = role.roleName;
						that.dataForm.desc = role.desc;
						that.dataForm.permissions = JSON.parse(role.permissions);
						// 保存原始权限数据
						that.oldPermissions = JSON.parse(role.permissions);
						defaultPermissions = role.defaultPermissions;
					});
				}
				that.$http('/permission/searchAllPermissions', 'GET', null, true, function(resp) {
					let temp = [];
					for (let one of resp.permissions) {
						let disabled = false;
						if (that.systemic) {
							disabled = defaultPermissions.includes(one.id);
						}
						temp.push({ key: one.id, label: `${one.moduleName}（${one.actionName}）`, disabled: disabled });
					}
					that.permissionList = temp;
				});
			});
		},
		dataFormSubmit: function () {
			let that = this
			that.$refs['dataForm'].validate(valid => {
				if (valid) {
					//因为用户是随机选择权限，所以对选中的权限排序
					that.dataForm.permissions.sort(function(a, b) {
						return a - b;
					});
					//判断用户选择的权限和原来的权限是否一致？把数组转换成字符串，简化比较两个数组是否一致
					// 如果只是修改角色名字或者备注，是不需要把该角色关联的所有用户都踢下线的，毕竟角色的权限没有改变。
					// 只有角色权限改变的时候，才需要后端程序把角色关联的所有用户都踢下线
					if (that.dataForm.permissions.join() != that.oldPermissions.join()) {
						that.dataForm.changed = true;
					} else {
						that.dataForm.changed = false;
					}
					that.$http(`/role/${!that.dataForm.id ? 'addRole' : 'updateRole'}`, 'POST', that.dataForm, true, function(resp) {
						if (resp.rows == 1) {
							that.$message({
								message: '操作成功',
								type: 'success',
								duration: 1200
							});
							that.visible = false;
							that.$emit('refreshDataList');
						} else {
							that.$message({
								message: '操作失败',
								type: 'error',
								duration: 1200
							});
						}
					});
				}
			})
		}
	}
};
</script>

<style lang="less" scoped="scoped">
.note {
	margin-left: 20px;
	color: #999;
}
</style>
