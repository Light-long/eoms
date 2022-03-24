<template>
    <div class="app-container">
        <el-row :gutter="20">
            <el-col :span="7" :xs="24">
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
                                    <span>用户名称</span>
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
