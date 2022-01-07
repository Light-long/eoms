<template>
	<el-dialog width="800px" :close-on-click-modal="false" v-model="visible" :show-close="false" center>
		<div id="pdfDom">
			<img :src="qrCodeBase64" class="qrCode">
			<h2 class="title">费&nbsp;&nbsp;&nbsp;用&nbsp;&nbsp;&nbsp;报&nbsp;&nbsp;&nbsp;销&nbsp;&nbsp;&nbsp;单</h2>
			<div class="top-info-container">
				<span class="info">报销部门：{{ dept }}</span>
				<span class="info">报销人：{{ name }}</span>
				<span class="info">报销日期：{{ date }}</span>
			</div>
			<div class="reim-table-container">
				<table class="reim-table">
					<tr align="center">
						<th width="30%">报销项目</th>
						<th width="34%">备注</th>
						<th width="20%">类别</th>
						<th width="16%">金额</th>
					</tr>
					<tr align="center" v-for="one in content">
						<td align="left">{{one.title}}</td>
						<td align="left">{{one.desc}}</td>
						<td>{{one.type}}</td>
						<td align="left">{{one.money!=""?one.money+"元":""}}</td>
					</tr>
					<tr>
						<th align="center">报销合计</th>
						<td colspan="3">{{ amount }}元</td>
					</tr>
					<tr>
						<th align="center">人民币大写</th>
						<td colspan="3">{{ smalltoBIG(amount) }}</td>
					</tr>
					<tr>
						<td colspan="5">
							<div class="info-container">
								<span class="info">借款金额：{{ anleihen }}元</span>
								<span class="info">应退金额：{{ money_1 }}元</span>
								<span class="info">应补金额：{{ money_2 }}元</span>
							</div>
						</td>
					</tr>
				</table>
			</div>
			<div class="bottom-info-container"></div>
			<div class="bottom-info-container">
				<span class="sig">会计主管：</span>
				<span class="sig">复核：</span>
				<span class="sig">出纳：</span>
				<span class="sig">报销人：</span>
			</div>
		</div>
		<template #footer>
			<span class="dialog-footer">
				<el-button type="primary" @click="getPdf('#pdfDom')" size="medium">下载报销单</el-button>
				<el-button size="medium" @click="cancel()">关闭窗口</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
export default {
	data: function() {
		return {
			visible: false,
			htmlTitle: '费用报销单',
			dept: null,
			name: null,
			date: null,
			amount: null,
			balance: null,
			anleihen: null,
			money_1: 0,
			money_2: 0,
			content:[],
			qrCodeBase64:null
		};
	},
	methods: {
		

		smalltoBIG: function(n) {
			var fraction = ['角', '分'];
			var digit = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
			var unit = [['元', '万', '亿'], ['', '拾', '佰', '仟']];
			var head = n < 0 ? '欠' : '';
			n = Math.abs(n);

			var s = '';

			for (var i = 0; i < fraction.length; i++) {
				s += (digit[Math.floor(n * 10 * Math.pow(10, i)) % 10] + fraction[i]).replace(/零./, '');
			}
			s = s || '整';
			n = Math.floor(n);

			for (var i = 0; i < unit[0].length && n > 0; i++) {
				var p = '';
				for (var j = 0; j < unit[1].length && n > 0; j++) {
					p = digit[n % 10] + unit[1][j] + p;
					n = Math.floor(n / 10);
				}
				s = p.replace(/(零.)*零$/, '').replace(/^$/, '零') + unit[0][i] + s;
			}
			return (
				head +
				s
					.replace(/(零.)*零元/, '元')
					.replace(/(零.)+/g, '零')
					.replace(/^整$/, '零元整')
			);
		}
	}
};
</script>

<style lang="less" scoped="scoped">
@import url('reim_pdf.less');
</style>
