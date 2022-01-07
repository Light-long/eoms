<template>
	<el-dialog
		:title="!dataForm.id ? '新增' : '修改'"
		v-if="isAuth(['ROOT', 'MEETING_ROOM:INSERT', 'MEETING_ROOM:UPDATE'])"
		:close-on-click-modal="false"
		v-model="visible"
		width="430px"
	>
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="80px">
			<el-form-item label="房间名称" prop="name">
				<el-input v-model="dataForm.name" size="medium" style="width:100%" clearable />
			</el-form-item>
			<el-form-item label="人数上限" prop="max">
				<el-input v-model="dataForm.max" size="medium" style="width:100%" clearable />
			</el-form-item>
			<el-form-item label="备注" prop="desc">
				<el-input v-model="dataForm.desc" style="width:100%" size="medium" maxlength="20" clearable />
			</el-form-item>
			<el-form-item v-if="dataForm.id" label="状态">
				<el-select v-model="dataForm.status" class="input" placeholder="状态" size="medium">
					<el-option label="可使用" value="1" />
					<el-option label="已停用" value="0" />
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
export default {
	data: function() {
		return {
			visible: false,
			dataForm: {
				id: null,
				name: null,
				max: null,
				desc: null,
				status: 1
			},
			dataRule: {
				name: [{ required: true, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{2,20}$', message: '会议室名称格式错误' }],
				max: [{ required: true, pattern: '^[1-9]\\d{0,4}$', message: '数字格式错误' }]
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
					that.$http('meeting_room/searchById', 'POST', { id: id }, true,function(resp) {
						that.dataForm.name = resp.name;
						that.dataForm.max = resp.max+"";
						that.dataForm.desc = resp.desc;
						that.dataForm.status=resp.status+"";
					});
				}
			});

			
		},
		
	}
};
</script>

<style lang="less">
.el-form {
	margin-left: 0px;
	margin-right: 10px;
}
.el-dialog__footer {
	margin-right: 10px;
}
.note {
	margin-left: 20px;
	color: #999;
}
</style>
