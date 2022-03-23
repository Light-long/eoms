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
                                <img src="https://online-office-1257796177.cos.ap-beijing.myqcloud.com/img/avatar/user.jpg" class="avatar-show">
                                <!--更换头像-->
                                <el-button type="primary" size="mini" style="display: block; margin-top: 5px" @click="updateAvatar()">更换头像</el-button>
                            </div>
                        </div>
                        <ul class="list-group list-group-striped">
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="user_fill" class="icon-svg-3"></SvgIcon>
                                    <span>用户名称</span>
                                    <div class="pull-right">李世龙</div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="phone" class="icon-svg-4"></SvgIcon>
                                    <span>手机号码</span>
                                    <div class="pull-right">1333333333</div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="email" class="icon-svg-4"></SvgIcon>
                                    <span>用户邮箱</span>
                                    <div class="pull-right">lishilong1108@foxmail.com</div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="warehouse_fill" class="icon-svg-3"></SvgIcon>
                                    <span>所属部门</span>
                                    <div class="pull-right">管理部</div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="role_fill" class="icon-svg-3"></SvgIcon>
                                    <span>所属角色</span>
                                    <div class="pull-right">总经理</div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="time" class="icon-svg-4"></SvgIcon>
                                    <span>创建日期</span>
                                    <div class="pull-right">2022-02-02</div>
                                </div>
                            </li>
                        </ul>
                    </div>
                </el-card>
            </el-col>
        </el-row>
        <AvatarUpdate v-if="avatarUpdateVisible" ref="avatarUpdate" @refreshDataList=""></AvatarUpdate>
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
                avatarUpdateVisible: false
            };
        },
        created() {

        },
        methods: {
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
