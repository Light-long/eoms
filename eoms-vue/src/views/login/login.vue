<template>
	<div class="login">
		<el-form ref="loginForm" class="login-form">
			<h3 class="title">企业办公管理系统</h3>
			<el-form-item prop="username">
				<el-input
						v-model="username"
						type="text"
						auto-complete="off"
						clearable="clearable"
						placeholder="账号"
						prefix-icon="el-icon-user"
				>
				</el-input>
			</el-form-item>
			<el-form-item prop="password">
				<el-input
						v-model="password"
						type="password"
						clearable="clearable"
						auto-complete="off"
						placeholder="密码"
						prefix-icon="el-icon-lock"
				>
				</el-input>
			</el-form-item>
			<el-form-item prop="code">
				<el-input
						v-model="code"
						auto-complete="off"
						placeholder="验证码"
						style="width: 63%"
				>
				</el-input>
				<div class="login-code">
					<img alt="点击更换验证码" :src="codeUrl" @click="getCode" class="login-code-img"/>
				</div>
			</el-form-item>
			<el-form-item style="width:100%;">
				<el-button
						:loading="loading"
						size="medium"
						type="primary"
						style="width:100%;"
						@click="login()"
				>
					<span v-if="!loading">登 录 系 统</span>
					<span v-else>登 录 中...</span>
				</el-button>
<!--				<div style="float: right;">-->
<!--					<router-link class="link-type" :to="'/register'">立即注册</router-link>-->
<!--				</div>-->
			</el-form-item>
		</el-form>
		<!--  底部  -->
		<div class="el-login-footer">
			<span>Copyright © 2022 Light-long All Rights Reserved.</span>
		</div>
	</div>
</template>

<script>
import 'element-plus/lib/theme-chalk/display.css';
import router from '../../router/index.js';
import {isUsername, isPassword} from "../../utils/validate";
import $ from 'jquery';

export default {
	data: function() {
		return {
			codeUrl: "",
			username: null,
			password: null,
			code: null
		};
	},
	created: function() {
		this.getCode()
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
				if (that.code == null || that.code === '') {
					that.$message({
						message: '验证码不能为空',
						type: 'error',
						duration: 1200
					});
				} else {
					// 校验验证码
					let that = this
					that.$http('/user/checkCode', 'POST', {code: that.code}, true, function (resp) {
						if (resp.code === 200) {
							// 验证码正确登录
							let data = {username: that.username, password: that.password}
							that.$http("/user/login", "POST", data, true, function(resp) {
								if (resp.code === 200) {
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
						} else {
							that.$message({
								message: resp.message,
								type: 'error',
								duration: 1000
							})
							// 刷新验证码
							that.getCode()
						}
					})
				}
			}
		},
		getCode: function () {
			let that = this
			$.ajax({
				url: 'http://localhost:8090/eoms-api' + '/user/getVerifyCode',
				type: 'POST',
				dataType: 'json',
				contentType: "application/json",
				xhrFields: {
					withCredentials: true
				},
				token: {
					token: localStorage.getItem("token")
				},
				async: true,
				data: null,
				success: function(resp) {
					that.codeUrl = "data:image/jpg;base64," + resp.code
				}
			})
		}
	}
};
</script>

<style lang="less" scoped="scoped">
	/*@import url('login.less');*/
	.login {
		display: flex;
		justify-content: center;
		align-items: center;
		height: 100%;
		background-image: url("../../assets/bg.jpg");
		background-size: cover;
	}
	.title {
		margin: 0px auto 30px auto;
		text-align: center;
		color: #707070;
		font-size: 22px;
	}

	.login-form {
		border-radius: 8px;
		background: #ffffff;
		width: 400px;
		padding: 25px 25px 5px 25px;
		.el-input {
			height: 38px;
			input {
				height: 38px;
			}
		}
		.input-icon {
			height: 39px;
			width: 14px;
			margin-left: 2px;
		}
	}
	.login-tip {
		font-size: 13px;
		text-align: center;
		color: #bfbfbf;
	}
	.login-code {
		width: 33%;
		height: 38px;
		float: right;
		img {
			cursor: pointer;
			vertical-align: middle;
		}
	}
	.el-login-footer {
		height: 40px;
		line-height: 40px;
		position: fixed;
		bottom: 0;
		width: 100%;
		text-align: center;
		color: #fff;
		font-family: Arial;
		font-size: 12px;
		letter-spacing: 1px;
	}
	.login-code-img {
		height: 38px;
		width: 115px;
	}
</style>
