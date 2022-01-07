<template>
	<el-dialog
		:title="!dataForm.id ? '新增' : '修改'"
		v-if="isAuth(['ROOT'])"
		:close-on-click-modal="false"
		v-model="visible"
		width="420px"
	>
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="80px">
			<el-form-item label="违纪类型" prop="type">
				<el-input v-model="dataForm.type" size="medium" style="width:100%" clearable />
			</el-form-item>
			<el-form-item label="罚款金额" prop="money">
				<el-input v-model="dataForm.money" size="medium" style="width:100%" clearable />
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
				type: null,
				money: null
			},
			dataRule: {
				type: [{ required: true, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{2,10}$', message: '违纪类型格式错误' }],
				money: [
					{
						required: true,
						pattern: '(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)',
						message: '罚款金额格式错误'
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
					that.$http('amect_type/searchById', 'POST', { id: id }, true, function(resp) {
						that.dataForm.type = resp.type;
						that.dataForm.money = resp.money+"";
					});
				}
			});
		},
		
	}
};
</script>

<style lang="less" scoped="scoped"></style>
