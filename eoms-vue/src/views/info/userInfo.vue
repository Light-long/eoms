<template>
    <div class="app-container">
        <el-row :gutter="20">
            <el-col :span="10" :xs="24">
                <el-card class="box-card">
                    <div slot="header" class="clearfix">
                        <span style="font-size: 18px; font-weight: bold">个人信息</span>
                    </div>
                    <el-divider />
                    <div>
                        <div class="text-center">
                            <!--显示头像-->
                            <div align="center">
                                <img :src="user.photo" class="avatar-show">
                                <!--更换头像-->
                                <el-button type="primary" size="mini" style="display: block; margin-top: 5px" @click="updateAvatar()">更换头像</el-button>
                            </div>
                        </div>
                        <ul class="list-group list-group-striped">
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="user_fill" class="icon-svg-3"></SvgIcon>
                                    <span>用户姓名</span>
                                    <div class="pull-right">{{user.name}}</div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="phone" class="icon-svg-4"></SvgIcon>
                                    <span>手机号码</span>
                                    <div class="pull-right">{{user.tel}}</div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="email" class="icon-svg-4"></SvgIcon>
                                    <span>用户邮箱</span>
                                    <div class="pull-right">{{user.email}}</div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="warehouse_fill" class="icon-svg-3"></SvgIcon>
                                    <span>所属部门</span>
                                    <div class="pull-right">{{user.deptName}}</div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="role_fill" class="icon-svg-3"></SvgIcon>
                                    <span>所属角色</span>
                                    <div class="pull-right">{{user.roles}}</div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="time" class="icon-svg-4"></SvgIcon>
                                    <span>入职日期</span>
                                    <div class="pull-right">{{user.hiredate}}</div>
                                </div>
                            </li>
                        </ul>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="13" :xs="24" style="margin-left: 30px">
                <el-card class="box-card">
                    <div slot="header" class="clearfix">
                        <span style="font-size: 18px; font-weight: bold">基本资料</span>
                    </div>
                    <el-divider />
                    <el-form ref="dataForm" :model="user" :rules="dataRule" label-width="80px">
                        <el-form-item label="用户昵称" prop="nickname">
                            <el-input v-model="user.nickname" maxlength="30" />
                        </el-form-item>
                        <el-form-item label="用户姓名" prop="name">
                            <el-input v-model="user.name" maxlength="30" />
                        </el-form-item>
                        <el-form-item label="手机号码" prop="tel">
                            <el-input v-model="user.tel" maxlength="11" />
                        </el-form-item>
                        <el-form-item label="邮箱" prop="email">
                            <el-input v-model="user.email" maxlength="50" />
                        </el-form-item>
                        <el-form-item label="性别" prop="sex">
                            <el-radio-group v-model="user.sex">
                                <el-radio label="男">男</el-radio>
                                <el-radio label="女">女</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" size="mini" @click="submit">保存</el-button>
                        </el-form-item>
                    </el-form>
                </el-card>
            </el-col>
        </el-row>
        <AvatarUpdate v-if="avatarUpdateVisible" ref="avatarUpdate" @refreshDataList="loadUserProfile"></AvatarUpdate>
    </div>
</template>

<script>
    import SvgIcon from "../../components/SvgIcon.vue";
    import AvatarUpdate from './avatar-update.vue'
    export default {
        name: "userInfo",
        components: {
            SvgIcon,
            AvatarUpdate
        },
        data() {
            return {
                avatarUpdateVisible: false,
                user: {},
                dataRule: {
                    name: [{ required: true, pattern: '^[\u4e00-\u9fa5]{2,10}$', message: '姓名格式错误' }],
                    sex: [{ required: true, message: '性别不能为空' }],
                    tel: [{ required: false, pattern: '^1\\d{10}$', message: '电话格式错误' }],
                    email: [
                        {
                            required: true,
                            pattern: '^([a-zA-Z]|[0-9])(\\w|\\-)+@[a-zA-Z0-9]+\\.([a-zA-Z]{2,4})$',
                            message: '邮箱格式错误'
                        }
                    ]
                }
            };
        },
        created() {
            this.loadUserProfile()
        },
        methods: {
            loadUserProfile: function() {
                let that = this
                that.$http('/user/searchUserProfile', 'GET', null, true, function (resp) {
                    that.user = resp.user
                })
            },
            updateAvatar: function () {
                this.avatarUpdateVisible = true
                this.$nextTick(() => {
                    this.$refs.avatarUpdate.init()
                })
            },
            submit: function () {
                let that = this
                let data = {
                    nickname: that.user.nickname,
                    name: that.user.name,
                    tel: that.user.tel,
                    sex: that.user.sex,
                    email: that.user.email
                }
                this.$refs['dataForm'].validate(valid => {
                    if (valid) {
                        that.$http('/user/updateBasicProfile', 'POST', data, true, function (resp) {
                            if (resp.rows === 1) {
                                that.$message({
                                    message: '修改成功',
                                    type: 'success',
                                    duration: 1200
                                });
                                this.loadUserProfile()
                            } else {
                                that.$message({
                                    message: '修改失败',
                                    type: 'error',
                                    duration: 1200
                                });
                            }
                        })
                    } else {
                        return false
                    }
                })
            }
        }
    };
</script>

<style lang="scss" scoped>
    @import "src/assets/scss/ruoyi";

    .icon-svg-3 {
        width: 1.5em;
        height: 1.5em;
        fill: currentColor;
        overflow: hidden;
        margin-right: 5px;
    }

    .icon-svg-4 {
        width: 1.3em;
        height: 1.3em;
        fill: currentColor;
        overflow: hidden;
        margin-right: 5px;
    }

    .avatar-show {
        width: 150px;
        height: 150px;
        border-radius: 50%;
        box-shadow: 0 0 4px #ccc;
    }
</style>
