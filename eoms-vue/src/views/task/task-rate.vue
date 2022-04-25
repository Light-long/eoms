<template>
	<el-dialog title="任务评分" :close-on-click-modal="false" v-model="visible"
			   width="500px">
		<el-row :gutter="20">
			<el-col :span="24" :xs="24">
				<el-card class="box-card" shadow="hover">
					<div align="center" style="margin-bottom: 10px;font-weight: bold;color: #409EFF">1星最低，5星最高</div>
					<div align="center">
						<el-rate v-model="dataForm.rate" size="large" />
					</div>
				</el-card>
			</el-col>
		</el-row>
		<template #footer>
			<div class="dialog-footer">
				<el-button type="common" size="small" @click="visible = false">取消</el-button>
				<el-button type="primary" size="small" @click="submitRate()">提交评分</el-button>
			</div>
		</template>
	</el-dialog>
</template>

<script>
	import SvgIcon from "../../components/SvgIcon.vue";
export default {
	components: {
		SvgIcon
	},
	data: function() {
		return {
			visible: false,
			dataForm: {
				id: null,
				rate: null
			}
		};
	},
	methods: {
		init: function(id) {
			let that = this;
			that.visible = true;
			that.dataForm.id = id
		},
		submitRate: function () {
			let that = this
			let data = {
				id: that.dataForm.id,
				rate: that.dataForm.rate
			}
			that.$http('/task/taskRate', 'POST', data, true, function (resp) {
				if (resp.rows === 1) {
					that.visible = false
					that.$message({
						message: '评分成功',
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
		}
	}
};
</script>

<style lang="less" scoped="scoped">

</style>
