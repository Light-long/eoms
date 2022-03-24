<template>
	<div class="app-container home">
		<!--banner-->
		<el-carousel indicator-position="outside">
			<el-carousel-item>
				<img src="../../assets/home/banner.jpg">
			</el-carousel-item>
		</el-carousel>
		<el-divider/>
		<el-row :gutter="20">
			<!--公告栏-->
			<el-col :span="10" :xs="24">
				<el-card class="update-log">
					<div class="demo-collapse">
						<h2 align="center" style="font-size:22px; font-weight: bold; color: rgb(61, 82, 102)">公告栏</h2>
						<el-collapse accordion>
							<el-collapse-item :name="index" v-for="(notice,index) in dataList">
								<template #title>
									<span style="font-size: 16px; font-weight: bold; color: rgb(61, 82, 102)">{{notice.title}}</span>
									<span style="font-size: 14px; font-weight: bold; color: rgb(61, 82, 102); margin-left: 10px">{{notice.updateTime}}</span>
									<el-tag class="ml-2" size="small" type="success" style="margin-left: 20px" v-if="notice.isTopping === 1">置顶</el-tag>
								</template>
								<div style="font-size: 14px; font-weight: normal;">
									{{notice.content}}
								</div>
							</el-collapse-item>
						</el-collapse>
					</div>
				</el-card>
			</el-col>
			<!--公司发展历程-->
			<el-col :span="14" :xs="24">
				<el-card class="update-log">
					<div class="demo-collapse">
						<h2 align="center" style="font-size: 22px; font-weight: bold; color: rgb(61, 82, 102)">公司发展历程</h2>
					</div>
					<hr>
					<el-timeline>
						<el-timeline-item timestamp="2022/2/12" placement="top" :type="'info'">
							<el-card>
								<h4>完成天使轮融资</h4>
								<p>2022年2月12日，本公司完成了由tx领投的300w的天使轮融资</p>
							</el-card>
						</el-timeline-item>
						<el-timeline-item timestamp="2022/3/12" placement="top" :type="'primary'">
							<el-card>
								<h4>完成A轮融资</h4>
								<p>2022年3月12日，本公司完成了由tx领投的800w的A轮融资</p>
							</el-card>
						</el-timeline-item>
						<el-timeline-item timestamp="2022/4/12" placement="top" :type="'success'">
							<el-card>
								<h4>完成B轮融资</h4>
								<p>2022年4月12日，本公司完成了由tx领投的2000w的B轮融资</p>
							</el-card>
						</el-timeline-item>
					</el-timeline>
				</el-card>
			</el-col>
		</el-row>
	</div>
</template>

<script>
	export default {
		name: "home",
		data: function () {
			return {
				dataList: []
			}
		},
		created: function() {
			this.loadNoticeList()
		},
		methods: {
			loadNoticeList: function () {
				let that = this
				that.$http('/notice/searchNoticeHome', 'GET', null, true, function (resp) {
					if (resp.code === 200) {
						that.dataList = resp.noticeList
					} else {
						that.$message({
							message: '加载公告栏出错',
							type: 'error',
							duration: 1200
						});
					}
				})
			}
		}
	}
</script>

<style lang="less" scoped="scoped">
	.home {
		blockquote {
			padding: 10px 20px;
			margin: 0 0 20px;
			font-size: 17.5px;
			border-left: 5px solid #eee;
		}
		hr {
			margin-top: 20px;
			margin-bottom: 20px;
			border: 0;
			border-top: 1px solid #eee;
		}
		.col-item {
			margin-bottom: 20px;
		}

		ul {
			padding: 0;
			margin: 0;
		}

		font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
		font-size: 13px;
		color: #676a6c;
		overflow-x: hidden;

		ul {
			list-style-type: none;
		}

		h4 {
			margin-top: 0px;
		}

		h2 {
			margin-top: 10px;
			font-size: 26px;
			font-weight: 100;
		}

		p {
			margin-top: 10px;

			b {
				font-weight: 700;
			}
		}

		.update-log {
			ol {
				display: block;
				list-style-type: decimal;
				margin-block-start: 1em;
				margin-block-end: 1em;
				margin-inline-start: 0;
				margin-inline-end: 0;
				padding-inline-start: 40px;
			}
		}
	}
</style>
