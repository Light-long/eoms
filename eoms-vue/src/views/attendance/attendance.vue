<template>
    <div class="app-container">
        <el-row :gutter="20">
            <el-col :span="24" :xs="24">
                <el-card shadow="none" class="box-card">
                    <!--基本信息-->
                    <div>
                        <div class="text-center">
                            <!--显示头像-->
                            <div align="center">
                                <el-tooltip
                                        class="box-item"
                                        effect="dark"
                                        placement="bottom"
                                        style="float: left"
                                >
                                    <template #content>先签到再签退<br />每天早上08::00开始签到</template>
                                    <el-button>考勤须知</el-button>
                                </el-tooltip>
                                <el-button
                                    size="small"
                                    type="primary"
                                    style="float: right;"
                                >
                                    本月考勤记录
                                </el-button>
                                <img :src="user.photo" class="avatar-show" style="margin-left: 10px">
                                <div style="font-weight: bold; font-size: 15px; margin-top: 10px; margin-bottom: 5px">{{user.deptName}} - {{user.name}}</div>
                                <div style="font-weight: bold; font-size: 15px; margin-top: 10px; margin-bottom: 5px">{{nowTime}}</div>
                            </div>
                        </div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="12" :xs="24">
                <el-card shadow="none" class="box-card">
                    <!--签到-->
                    <el-card class="box-card">
                        <div slot="header" class="clearfix">
                            <span style="font-size: 20px; font-weight: bold">签到</span>
                        </div>
                        <ul class="list-group list-group-striped">
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="" class="icon-svg-3"></SvgIcon>
                                    <span>开始时间</span>
                                    <div class="pull-right">
                                        <el-tag size="small" type="primary">{{attendanceTimeMap.signInStartTime}}</el-tag>
                                    </div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="" class="icon-svg-3"></SvgIcon>
                                    <span>结束时间</span>
                                    <div class="pull-right">
                                        <el-tag size="small" type="warning">{{attendanceTimeMap.signInEndTime}}</el-tag>
                                    </div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="" class="icon-svg-3"></SvgIcon>
                                    <span>签到时间</span>
                                    <div class="pull-right">
                                        <el-tag v-if="signInTime != null" size="small" type="warning">{{signInTime}}</el-tag>
                                    </div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="" class="icon-svg-3"></SvgIcon>
                                    <span>签到结果</span>
                                    <div class="pull-right">
                                        <el-tag v-if="signInResult === '未签到'" size="small" type="info">{{signInResult}}</el-tag>
                                        <el-tag v-if="signInResult === '迟到'" size="small" type="danger">{{signInResult}}</el-tag>
                                        <el-tag v-if="signInResult === '正常'" size="small" type="success">{{signInResult}}</el-tag>
                                    </div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center" align="center">
                                    <el-button
                                        :disabled="disableSignInBtn"
                                        type="primary"
                                        size="medium"
                                        style="display: block; margin-top: 5px"
                                        @click="signIn">{{signInButtonText}}</el-button>
                                </div>
                            </li>
                        </ul>
                    </el-card>
                </el-card>
            </el-col>
            <el-col :span="12" :xs="24">
                <el-card shadow="none" class="box-card">
                    <!--签退-->
                    <el-card class="box-card">
                        <div slot="header" class="clearfix">
                            <span style="font-size: 20px; font-weight: bold">签退</span>
                        </div>
                        <ul class="list-group list-group-striped">
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="" class="icon-svg-3"></SvgIcon>
                                    <span>开始时间</span>
                                    <div class="pull-right">
                                        <el-tag size="small" type="primary">{{attendanceTimeMap.signOutStartTime}}</el-tag>
                                    </div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="" class="icon-svg-3"></SvgIcon>
                                    <span>结束时间</span>
                                    <div class="pull-right">
                                        <el-tag size="small" type="warning">{{attendanceTimeMap.signOutEndTime}}</el-tag>
                                    </div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="" class="icon-svg-3"></SvgIcon>
                                    <span>签退时间</span>
                                    <div class="pull-right">
                                        <el-tag v-if="signOutTime != null" size="small" type="warning">{{signOutTime}}</el-tag>
                                    </div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center">
                                    <SvgIcon name="" class="icon-svg-3"></SvgIcon>
                                    <span>签退结果</span>
                                    <div class="pull-right">
                                        <el-tag v-if="signOutResult === '未签退'" size="small" type="info">{{signOutResult}}</el-tag>
                                        <el-tag v-if="signOutResult === '早退'" size="small" type="danger">{{signOutResult}}</el-tag>
                                        <el-tag v-if="signOutResult === '正常'" size="small" type="success">{{signOutResult}}</el-tag>
                                        <el-tag v-if="signOutResult === '加班'" size="small" type="primary">{{signOutResult}}</el-tag>
                                    </div>
                                </div>
                            </li>
                            <li class="list-group-item">
                                <div style="line-height: 20px; vertical-align: center" align="center">
                                    <el-button
                                        :disabled="disableSignOutBtn"
                                        type="primary"
                                        size="medium"
                                        style="display: block; margin-top: 5px"
                                        @click="signOut">{{signOutButtonText}}</el-button>
                                </div>
                            </li>
                        </ul>
                    </el-card>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script>
    import SvgIcon from "../../components/SvgIcon.vue";
    export default {
        name: "attendance",
        components: {
            SvgIcon
        },
        data() {
            return {
                nowTime: dayjs(new Date()).format('YYYY-MM-DD HH:MM:ss'),
                address: null,
                user: {},
                attendanceTimeMap: {},
                // canSignIn: {},
                signInButtonText: '签到',
                disableSignInBtn: false,
                signInTime: null,
                signInResult: null,
                // canSignOut: {},
                signOutButtonText: '签退',
                disableSignOutBtn: false,
                signOutTime: null,
                signOutResult: null
            };
        },
        mounted() {
            let that = this
            setInterval(() => {
                that.nowTime = dayjs(new Date()).format('YYYY-MM-DD HH:MM:ss')
            }, 1000)
        },
        created() {
            this.loadUserProfile()
            this.getAllAttendanceTime()
            // this.validCanSignIn()
            // 查询签到结果
            this.searchSignInResult()
            // this.validCanSignOut()
            // 签退结果
            this.searchSignOutResult()
        },
        methods: {
            // 加载头像，基本信息
            loadUserProfile: function() {
                let that = this
                that.$http('/user/searchUserProfile', 'GET', null, true, function (resp) {
                    that.user = resp.user
                })
            },
            // 获取所有的考勤时间
            getAllAttendanceTime: function () {
                let that = this
                that.$http('/attendance/getAllAttendanceTime', 'GET', null, true, function (resp) {
                    that.attendanceTimeMap = resp.map
                })
            },
            // 判断是否能签到
            // validCanSignIn: function () {
            //     let that = this
            //     that.$http('/attendance/validCanSignIn', 'GET', null, true, function (result) {
            //         that.canSignIn = result.result
            //     })
            // },
            // 查询签到结果
            searchSignInResult: function () {
                let that = this
                that.$http('/attendance/searchSignInResult', 'GET', null, true, function (result) {
                    let info = result.info
                    if (info == null) {
                        that.signInResult = '未签到'
                    } else {
                        if (info.status === 1) {
                            that.signInResult = '正常'
                        } else if (info.status === 2) {
                            that.signInResult = '迟到'
                        }
                        that.signInTime = info.signInTime
                        that.disableSignInBtn = true
                        that.signInButtonText = '已签到'
                    }
                })
            },
            // 签到
            signIn: function () {
                let that = this
                that.$http('/attendance/signIn', 'GET', null, true, function (result) {
                    if (result.flag === '正常签到') {
                        that.$message({
                            message: '签到成功',
                            type: 'success',
                            duration: 1000
                        });
                    } else if (result.flag === '迟到签到'){
                        that.$message({
                            message: '您迟到了',
                            type: 'warning',
                            duration: 1000
                        });
                    }
                    setInterval(() => {
                        that.$router.go(0)
                    }, 500)
                })
            },
            // 判断是否能签退
            // validCanSignOut: function () {
            //     let that = this
            //     that.$http('/attendance/validCanSignOut', 'GET', null, true, function (resp) {
            //         that.canSignOut = resp.result
            //     })
            // },
            // 查询签退结果
            searchSignOutResult: function () {
                let that = this
                that.$http('/attendance/searchSignOutResult', 'GET', null, true, function (result) {
                    let info = result.info
                    if (info == null) {
                        that.signOutResult = '未签退'
                    } else {
                        if (info.status === 1) {
                            that.signOutResult = '正常'
                        } else if (info.status === 2) {
                            that.signOutResult = '早退'
                        } else if (info.status === 3) {
                            that.signOutResult = '加班'
                        }
                        that.signOutTime = info.signOutTime
                        that.disableSignOutBtn = true
                        that.signOutButtonText = '已签退'
                    }
                })
            },
            // 签退
            signOut: function () {
                let that = this
                that.$http('/attendance/signOut', 'GET', null, true, function (result) {
                    if (result.flag === '正常签退') {
                        that.$message({
                            message: '签退成功',
                            type: 'success',
                            duration: 1000
                        });
                    } else if (result.flag === '早退'){
                        that.$message({
                            message: '您早退了',
                            type: 'warning',
                            duration: 1000
                        });
                    } else if (result.flag === '加班') {
                        that.$message({
                            message: '加班辛苦了',
                            type: 'success',
                            duration: 1000
                        });
                    }
                    setInterval(() => {
                        that.$router.go(0)
                    }, 500)
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
        width: 120px;
        height: 120px;
        border-radius: 50%;
        box-shadow: 0 0 4px #ccc;
    }
</style>
