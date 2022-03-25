<template>
    <div class="login">
        <el-form :model="dataForm" ref="dataForm" class="login-form" :rules="dataRule">
            <h3 class="title">企业办公管理系统</h3>
            <el-form-item prop="username">
                <el-input
                        v-model="dataForm.username"
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
                        v-model="dataForm.password"
                        type="password"
                        clearable="clearable"
                        auto-complete="off"
                        placeholder="密码"
                        prefix-icon="el-icon-lock"
                >
                </el-input>
            </el-form-item>
            <el-form-item prop="confirmPassword">
                <el-input
                        v-model="dataForm.confirmPassword"
                        type="password"
                        clearable="clearable"
                        auto-complete="off"
                        placeholder="确认密码"
                        prefix-icon="el-icon-lock"
                >
                </el-input>
            </el-form-item>
            <el-form-item prop="code">
                <el-input
                        v-model="dataForm.code"
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
                        @click="register()"
                >
                    <span v-if="!loading">注 册</span>
                    <span v-else>注 册 中...</span>
                </el-button>
                <div style="float: right;">
                    <router-link class="link-type" :to="'/login'">使用已有账号登录</router-link>
                </div>
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
    import $ from 'jquery';

    export default {
        data: function() {
            const validateConfirmPassword = (rule, value, callback) => {
                if (value !== this.dataForm.password) {
                    callback(new Error('两次输入的密码不一致'));
                } else {
                    callback();
                }
            }
            return {
                codeUrl: "",
                dataForm: {
                    username: null,
                    password: null,
                    confirmPassword: null,
                    code: null
                },
                dataRule: {
                    username: [{required: true, trigger: "blur", pattern: '^[a-zA-Z0-9]{5,20}$', message: '用户名格式错误' }],
                    newPassword: [{ required: true, trigger: "blur", pattern: '^[a-zA-Z0-9]{5,20}$', message: '密码格式错误' }],
                    confirmPassword: [
                        { required: true, pattern: '^[a-zA-Z0-9]{5,20}$', message: '密码格式错误' },
                        { validator: validateConfirmPassword, trigger: 'blur' }
                    ],
                    code: [{ required: true, trigger: 'blur', message: '验证码不能为空'}]
                }
            };
        },
        created: function() {
            this.dataForm = {}
            this.getCode()
        },
        methods: {
            register: function() {
                let that = this
                this.$refs['dataForm'].validate(valid => {
                    if (valid) {
                        // 校验验证码
                        that.$http('/user/checkCode', 'POST', {code: that.dataForm.code}, true, function (resp) {
                            if (resp.code === 200) {
                                // 验证码正确登录
                                let data = {
                                    username: that.dataForm.username,
                                    password: that.dataForm.password
                                }
                                that.$http("/user/register", "POST", data, true, function(resp) {
                                    if (resp.rows === 1) {
                                        that.$message({
                                            message: '注册成功',
                                            type: 'success',
                                            duration: 1000
                                        })
                                        router.push({name: 'Login'})
                                    } else {
                                        that.$message({
                                            message: '注册失败',
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
                    } else {
                        return false
                    }
                })
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
                        that.dataForm.code = null
                    }
                })
            }
        }
    };
</script>

<style lang="less" scoped="scoped">
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

    .link-type,
    .link-type:focus {
        color: #337ab7;
        cursor: pointer;

        &:hover {
            color: rgb(32, 160, 255);
        }
    }
</style>
