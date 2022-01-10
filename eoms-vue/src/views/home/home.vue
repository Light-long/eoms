<template>
	<div class="home">
		<el-row :gutter="70" type="flex" justify="center">
			<el-col :span="9" class="left">
				<h2>Emos在线办公系统</h2>
				<div class="desc">
					<p>轻松工作&nbsp;&nbsp;高效办公</p>
					<p>协同办公&nbsp;&nbsp;让工作更简单</p>
				</div>
				<div class="bottom">
					<div class="remark-container">
						<div class="ball blue">1</div>
						<div class="remark">先进的技术</div>
					</div>
					<div class="remark-container">
						<div class="ball red">2</div>
						<div class="remark">强大的团队</div>
					</div>
					<div class="remark-container">
						<div class="ball green">3</div>
						<div class="remark">丰富的经验</div>
					</div>
				</div>
			</el-col>
			<el-col :span="11" class="right">
				<img src="src/assets/home/banner.png" class="banner" /></el-col>
		</el-row>
	</div>
</template>

<script></script>

<style lang="less" scoped="scoped">
	@import url('./home.less');
</style>
