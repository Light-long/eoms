<template>
	<el-dialog
		:title="!dataForm.id ? '新增' : '修改'"
		v-if="isAuth(['ROOT', 'DEPT:INSERT', 'DEPT:UPDATE'])"
		:close-on-click-modal="false"
		v-model="visible"
		width="420px"
	>
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="60px">
			<el-form-item label="部门" prop="deptName">
				<el-input v-model="dataForm.deptName" size="medium" style="width:100%" clearable />
			</el-form-item>
			<el-form-item label="电话" prop="tel">
				<el-input v-model="dataForm.tel" size="medium" style="width:100%" clearable />
			</el-form-item>
			<el-form-item label="邮箱" prop="email">
				<el-input v-model="dataForm.email" size="medium" style="width:100%" clearable />
			</el-form-item>
			<el-form-item label="备注" prop="desc">
				<el-input v-model="dataForm.desc" style="width:100%" size="medium" maxlength="20" clearable />
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
			dataForm: {
				id: null,
				deptName: null,
				tel: null,
				email: null,
				desc: null
			},
			dataRule: {
				deptName: [
					{ required: true, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{2,10}$', message: '部门名称格式错误' }
				],
				tel: [
					{
						required: false,
						pattern: '^1\\d{10}$|^(0\\d{2,3}\-){0,1}[1-9]\\d{6,7}$',
						message: '电话格式错误'
					}
				],
				email: [
					{
						required: false,
						pattern: '^([a-zA-Z]|[0-9])(\\w|\-)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$',
						message: '邮箱格式错误'
					}
				]
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
				if (id) {
					that.$http('dept/searchById', 'POST', { id: id },true, function(resp) {
						that.dataForm.deptName = resp.deptName;
						that.dataForm.tel = resp.tel;
						that.dataForm.email = resp.email;
						that.dataForm.desc = resp.desc;
					});
				}
			});
		},
		
	}
};
</script>

<style lang="less" scoped="scoped">
</style>
