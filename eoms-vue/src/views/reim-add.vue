<template>
	<el-dialog title="报销申请" :close-on-click-modal="false" v-model="visible" width="550px">
		<el-scrollbar height="470px">
			<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="100px">
				<el-form-item label="报销种类" prop="type">
					<el-radio-group v-model="dataForm.type" size="medium">
						<el-radio-button label="普通报销"></el-radio-button>
						<el-radio-button label="差旅报销"></el-radio-button>
					</el-radio-group>
				</el-form-item>
				<el-form-item label="借款金额" prop="anleihen">
					<el-input
						v-model="dataForm.anleihen"
						placeholder="借款金额"
						size="medium"
						style="width:160px;"
						clearable="clearable"
						prop="anleihen"
					/>
					<span class="note">请如实填写借款金额</span>
				</el-form-item>
				<div v-for="(one, $index) in dataForm.project" class="project">
					<h3>【 报销项目 】</h3>
					<i class="el-icon-delete icon-delete" @click="deleteProjectHandle($index)"></i>
					<el-form-item
						label="项目名称"
						:prop="'project.' + $index + '.title'"
						:rules="{
							required: true,
							message: '项目名称必填'
						}"
					>
						<el-input
							v-model="one.title"
							size="medium"
							style="width:95%"
							maxlength="20"
							clearable
						/>
					</el-form-item>
					<el-form-item
						label="项目类别"
						:prop="'project.' + $index + '.type'"
						:rules="{
							required: true,
							message: '项目类别必填'
						}"
					>
						<el-select v-model="one.type" class="input" size="medium" clearable>
							<el-option label="办公用品" value="办公用品" />
							<el-option label="招待费" value="招待费" />
							<el-option label="采购费" value="采购费" />
							<el-option label="劳务费" value="劳务费" />
							<el-option label="培训费" value="培训费" />
							<el-option label="维修费" value="维修费" />
							<el-option label="办公费" value="办公费" />
							<el-option label="其他" value="其他" />
						</el-select>
						<span class="note">请选择项目报销的类别</span>
					</el-form-item>
					<el-form-item label="备注信息" prop="projectDesc">
						<el-input
							v-model="one.desc"
							type="textarea"
							size="medium"
							maxlength="50"
							style="width:95%"
							resize="none"
							show-word-limit
							clearable
						/>
					</el-form-item>
					<el-form-item
						label="报销金额"
						:prop="'project.' + $index + '.money'"
						:rules="{
							required: true,
							pattern:
								'(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)',
							message: '报销金额必填'
						}"
					>
						<el-input
							v-model="one.money"
							size="medium"
							style="width:160px;"
							clearable
						/>
						<span class="note">认真核对该项目的报销金额</span>
					</el-form-item>
				</div>
			</el-form>
		</el-scrollbar>
		<template #footer>
			<span class="dialog-footer">
				<el-button type="danger" size="medium" @click="addHandle">添加项目</el-button>
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
				type: '普通报销',
				anleihen: null,
				project: []
			},
			dataRule: {
				anleihen: [
					{
						required: true,
						pattern:
							'(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)',
						message: '金额格式错误'
					}
				]
			}
		};
	},
	methods: {
		init: function() {
			let that = this;
			that.visible = true;
			that.$nextTick(() => {
				that.$refs['dataForm'].resetFields();
				that.dataForm.type = '普通报销';
				that.dataForm.anleihen = null;
				that.dataForm.project = [{ title: null, type: null, desc: null, money: null }];
			});
		},
		
		
	}
};
</script>

<style lang="less" scoped="scoped">
.note {
	color: #999;
	margin-left: 15px;
}
.project {
	border-top: dashed 1px #e0e0e0;
	position: relative;
}
h3 {
	text-align: center;
	margin: 20px 0;
}
.icon-delete {
	position: absolute;
	right: 20px;
	top: 20px;
	height: 24px;
	width: 24px;
	cursor: pointer;
}
</style>
