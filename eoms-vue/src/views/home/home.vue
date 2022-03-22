<template>
	<div class="app-container home">
		<el-row :gutter="20">
			<el-col>
				<el-card class="update-log">
					<div class="demo-collapse">
						<h2 align="center" style="font-size:30px; font-weight: bold; color: rgb(61, 82, 102)">公告栏</h2>
						<el-collapse accordion>
							<el-collapse-item name="{{notice.title}}" v-for="notice in dataList">
								<template #title>
									<span style="font-size: 20px; font-weight: bold; color: rgb(61, 82, 102)">{{notice.title}}</span>
									<span style="font-size: 15px; font-weight: bold; color: rgb(61, 82, 102); margin-left: 10px">{{notice.updateTime}}</span>
									<el-tag class="ml-2" size="small" type="success" style="margin-left: 20px" v-if="notice.isTopping === 1">置顶</el-tag>
								</template>
								<div style="font-size: 15px; font-weight: bold;">
									{{notice.content}}
								</div>
							</el-collapse-item>
						</el-collapse>
					</div>
				</el-card>
			</el-col>
		</el-row>
		<el-divider />
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
