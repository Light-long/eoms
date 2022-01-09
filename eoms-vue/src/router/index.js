import {
	createRouter,
	createWebHashHistory
}
from 'vue-router'
import Login from '../views/login/login.vue'
import Main from "../views/main.vue"
import Home from "../views/home.vue"
import Role from "../views/role/role.vue"
import User from "../views/user/user.vue"
import Dept from "../views/dept/dept.vue"
import MeetingRoom from '../views/meeting_room.vue'
import OfflineMeeting from "../views/offline_meeting.vue"
import OnlineMeeting from "../views/online_meeting.vue"
import MeetingVideo from "../views/meeting_video.vue"
import Approval from "../views/approval.vue"
import Leave from "../views/leave.vue"
import Amect from "../views/amect.vue"
import AmectType from "../views/amect_type.vue"
import AmectReport from "../views/amect_report.vue"
import Reim from "../views/reim.vue"
import NotFound from "../views/404.vue"


const routes = [
	{
		path: '/login',
		name: 'Login',
		component: Login
	},
	{
		path: '/',
		name: 'Main',
		component: Main,
		children: [{
				path: '/home',
				name: 'Home',
				component: Home,
				meta: {
					title: '首页',
				}
			},
			{
				path: "/role",
				name: "Role",
				component: Role,
				meta: {
					title: "角色管理",
					isTab: true
				}
			},
			{
				path: '/user',
				name: 'User',
				component: User,
				meta: {
					title: '用户管理',
					isTab: true
				}
			},
			{
				path: '/dept',
				name: 'Dept',
				component: Dept,
				meta: {
					title: '部门管理',
					isTab: true
				}
			},
			{
				path: '/meeting_room',
				name: 'MeetingRoom',
				component: MeetingRoom,
				meta: {
					title: '会议室',
					isTab: true
				}
			},
			{
				path: '/offline_meeting',
				name: 'OfflineMeeting',
				component: OfflineMeeting,
				meta: {
					title: '线下会议',
					isTab: true
				}
			},
			{
				path: '/online_meeting',
				name: 'OnlineMeeting',
				component: OnlineMeeting,
				meta: {
					title: '线上会议',
					isTab: true
				}
			},
			{
				path: '/meeting_video/:meetingId/:uuid',
				name: 'MeetingVideo',
				component: MeetingVideo,
				meta: {
					title: '视频会议',
					isTab: true
				}
			}, {
				path: '/approval',
				name: 'Approval',
				component: Approval,
				meta: {
					title: '在线审批',
					isTab: true
				}
			},
			{
				path: '/leave',
				name: 'Leave',
				component: Leave,
				meta: {
					title: '员工请假',
					isTab: true
				}
			},
			{
				path: '/amect',
				name: 'Amect',
				component: Amect,
				meta: {
					title: '违纪罚款',
					isTab: true
				}
			},
			{
				path: '/amect_type',
				name: 'AmectType',
				component: AmectType,
				meta: {
					title: '罚款类型',
					isTab: true
				}
			},
			{
				path: '/amect_report',
				name: 'AmectReport',
				component: AmectReport,
				meta: {
					title: '违纪报告',
					isTab: true
				}
			},
			{
				path: '/reim',
				name: 'Reim',
				component: Reim,
				meta: {
					title: '报销管理',
					isTab: true
				}
			}
		]
	},
	{
		path: "/404",
		name: "NotFound",
		component: NotFound
	},
	{
		path: '/:pathMatch(.*)*',
		redirect: "/404"
	}
]

const router = createRouter({
	history: createWebHashHistory(),
	routes
})
router.beforeEach((to, from, next) => {
	if (to.name != "Login") {
		let permissions = localStorage.getItem("permissions")
		if (permissions == null || permissions == "") {
			next({
				name: 'Login'
			})
		}
	}
	return next()
})

export default router
