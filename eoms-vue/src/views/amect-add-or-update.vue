<template>
	<el-dialog
		:title="!dataForm.id ? '新增罚款记录' : '修改罚款记录'"
		:close-on-click-modal="false"
		v-model="visible"
		width="692px"
	>
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="60px">
			<el-form-item label="类型" prop="typeId">
				<el-select v-model="dataForm.typeId" placeholder="罚款类型" size="medium" style="width:40%">
					<el-option v-for="one in amectTypeList" :label="one.type" :value="one.id" />
				</el-select>
				<span class="desc">必须选择一个罚款类型，罚款金额可以自动生成</span>
			</el-form-item>
			<el-form-item label="金额" prop="amount">
				<el-input v-model="dataForm.amount" size="medium" style="width:40%" placeholder="罚款金额" clearable />
				<span class="desc">元</span>
			</el-form-item>
			<el-form-item label="原因" prop="reason">
				<el-input
					type="textarea"
					:rows="2"
					placeholder="罚款原因"
					v-model="dataForm.reason"
					size="medium"
					resize="none"
					maxlength="200"
					show-word-limit
					clearable="clearable"
				/>
			</el-form-item>
			<el-form-item label="成员" prop="members" v-if="dataForm.id == 0">
				<el-transfer
					v-model="dataForm.members"
					:data="users"
					:titles="['员工', '当事人']"
					filterable
					filter-placeholder="请输入姓名"
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
			dataForm: {
				id: null,
				typeId: null,
				amount: null,
				reason: null,
				members: []
			},
			amectTypeList: [],
			users: [],
			dataRule: {
				typeId: [{ required: true, message: '罚款类型为必填' }],
				amount: [
					{
						required: true,
						pattern: '(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)',
						message: '罚款金额格式错误'
					}
				],
				reason: [{ required: true, message: '罚款原因为必填' }],
				members: [
					{ required: true, trigger: 'blur', message: '必须设置当事人' },
					{ required: false, trigger: 'change', message: '必须设置当事人' }
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
				that.$http('amect_type/searchAllAmectType', 'GET', {}, true, function(resp) {
					that.amectTypeList = resp.list;
				});
				that.$http('user/searchAllUser', 'GET', null, true, function(resp) {
					let temp = [];
					for (let one of resp.list) {
						temp.push({ key: one.id, label: one.name });
					}
					that.users = temp;
				});
				if (id) {
					that.$http('amect/searchById', 'POST', { id: id }, true, function(resp) {
						that.dataForm.typeId = resp.typeId;
						that.dataForm.amount = resp.amount+"";
						that.dataForm.reason = resp.reason;
					});
				}
			});
		},
		
	}
};
</script>

<style>
.desc {
	margin-left: 15px;
}
</style>
