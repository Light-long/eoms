<template>
	<div class="page">
		<el-row type="flex" justify="center" align="middle" class="container">
			<el-col :lg="14" :xl="10" class="hidden-md-and-down">
				<el-row class="panel">
					<el-col :span="12">
						<div class="left">
<!--							<img src="../../assets/login/logo.png" class="logo" />-->
							<img src="../../assets/login/big-1.png" class="big" />
						</div>
					</el-col>
					<el-col :span="12">
						<div class="right">
						<div class="title-container">
							<h2>Emos在线办公平台</h2>
							<span>( Ver 1.0 )</span>
							</div>
								<div class="row">
									<el-input
										v-model="username"
										placeholder="用户名"
										prefix-icon="el-icon-user"
										clearable="clearable"
									/>
								</div>
								<div class="row">
									<el-input
										type="password"
										v-model="password"
										placeholder="密码"
										prefix-icon="el-icon-lock"
										clearable="clearable"
									/>
							</div>
							<div class="row">
								<el-button type="primary" class="btn" @click="login">登陆系统</el-button>
							</div>
						</div>
					</el-col>
				</el-row>
			</el-col>
		</el-row>
	</div>
</template>

<script>
import 'element-plus/lib/theme-chalk/display.css';
import router from '../../router/index.js';
import {isUsername, isPassword} from "../../utils/validate";

export default {
	data: function() {
		return {
			username: null,
			password: null
		};
	},

	methods: {
		login: function() {
			let that = this
			if (!isUsername(that.username)) {
				that.$message({
					message: '用户名格式不正确',
					type: 'error',
					duration: 1200
				});
			} else if (!isPassword(that.password)) {
				that.$message({
					message: '密码格式不正确',
					type: 'error',
					duration: 1200
				});
			} else {
				let data = {username: that.username, password: that.password}
				that.$http("/user/login", "POST", data, true, function(resp) {
					if (resp.code == 200) {
						let permissions = resp.permissions
						let token = resp.token
						localStorage.setItem("permissions", permissions)
						localStorage.setItem("token", token)
						router.push({name: 'Home'})
						that.$message({
							message: '登陆成功',
							type: 'success',
							duration: 1000
						})
					} else {
						that.$message({
							message: '登陆失败',
							type: 'error',
							duration: 1200
						});
					}
				})
			}
		}
	}
};
</script>

<style lang="less" scoped="scoped">
	@import url('login.less');
</style>
