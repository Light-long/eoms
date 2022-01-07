<template>
	<el-dialog title="缴纳罚款" :close-on-click-modal="false" v-model="visible" width="305px" center>
		<img :src="qrCode" class="qrcode" v-if="!result" />
		<div v-if="result" class="pay-success">
			<el-result icon="success" title="付款成功" subTitle="请根据提示进行操作"></el-result>
		</div>
		<div class="dialog-footer-style">
			<el-button type="danger" size="medium" v-if="!result" @click="cancelHandle">取消支付</el-button>
			<el-button type="primary" size="medium" v-if="!result" @click="successHandle">支付成功</el-button>
			<el-button type="primary" size="medium" v-if="result" @click="closeHandle">关闭窗口</el-button>
		</div>
	</el-dialog>
</template>

<script>
export default {
	data: function() {
		return {
			visible: false,
			dataForm: {
				id: null
			},
			result: false,
			qrCode: null
		};
	},
	methods: {
		
	}
};
</script>

<style scoped>
.qrCode {
	width: 255px;
	height: 255px;
}
.pay-success {
	width: 255px;
	height: 255px;
}
.dialog-footer-style {
	padding-bottom: 30px;
	padding-top: 10px;
	text-align: center;
}
</style>
