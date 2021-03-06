import {
	createRouter,
	createWebHashHistory
}
from 'vue-router'
import Login from '../views/login/login.vue'
import Main from "../views/main.vue"
import Home from "../views/home/home.vue"
import Role from "../views/role/role.vue"
import User from "../views/user/user.vue"
import Dept from "../views/dept/dept.vue"
import MeetingRoom from '../views/meetingroom/meeting_room.vue'
import OfflineMeeting from "../views/offline_meeting/offline_meeting.vue"
import OnlineMeeting from "../views/online_meeting/online_meeting.vue"
import MeetingVideo from "../views/meeting_video/meeting_video.vue"
import Approval from "../views/approval/approval.vue"
import Leave from "../views/leave/leave.vue"
import Amect from "../views/amect/amect.vue"
import AmectType from "../views/amect/amect_type.vue"
import AmectReport from "../views/amect/amect_report.vue"
import Reim from "../views/reim/reim.vue"
import NotFound from "../views/404.vue"
import Todo from "../views/todo/todo.vue"
import Notice from "../views/notice/notice.vue"
import UserInfo from "../views/info/userInfo.vue"
import Register from "../views/login/register.vue"
import MailList from "../views/info/mail-list.vue"
import Meeting from "../views/meeting/meeting.vue"
import Attendance from "../views/attendance/attendance.vue"
import AttendanceRecord from '../views/attendance/attendanceRecord.vue'
import AttendanceStatistic from "../views/attendance/attendanceStatistic.vue";
import Document from "../views/document/document.vue";
import TaskAdmin from "../views/task/task-admin.vue"
import MyTask from "../views/task/my-task.vue"

const routes = [
	{
		path: '/login',
		name: 'Login',
		component: Login
	},
	{
		path: '/register',
		name: 'Register',
		component: Register
	},
	{
		path: '/',
		name: 'Main',
		component: Main,
		redirect: '/home',
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
				path: '/meeting',
				name: 'Meeting',
				component: Meeting,
				meta: {
					title: '会议列表',
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
			},
			{
				path: '/todo',
				name: 'Todo',
				component: Todo,
				meta: {
					title: '我的待办',
					isTab: true
				}
			},
			{
				path: '/document',
				name: 'Document',
				component: Document,
				meta: {
					title: '文件管理',
					isTab: true
				}
			},
			{
				path: '/attendance',
				name: 'Attendance',
				component: Attendance,
				meta: {
					title: '在线考勤',
					isTab: true
				}
			},
			{
				path: '/attendanceRecord',
				name: 'AttendanceRecord',
				component: AttendanceRecord,
				meta: {
					title: '我的考勤',
					isTab: true
				}
			},
			{
				path: '/attendanceStatistic',
				name: 'AttendanceStatistic',
				component: AttendanceStatistic,
				meta: {
					title: '考勤统计',
					isTab: true
				}
			},
			{
				path: '/notice',
				name: 'Notice',
				component: Notice,
				meta: {
					title: '公告管理',
					isTab: true
				}
			},
			{
				path: '/userInfo',
				name: 'UserInfo',
				component: UserInfo,
				meta: {
					title: '个人中心',
					isTab: true
				}
			},
			{
				path: '/mailList',
				name: 'MailList',
				component: MailList,
				meta: {
					title: '通讯录',
					isTab: true
				}
			},
			{
				path: '/myTask',
				name: 'MyTask',
				component: MyTask,
				meta: {
					title: '我的任务',
					isTab: true
				}
			},
			{
				path: '/taskAdmin',
				name: 'TaskAdmin',
				component: TaskAdmin,
				meta: {
					title: '任务管理',
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

	if (to.name !== "Login") {
		if (to.name === 'Register') {
			return next()
		} else {
			let permissions = localStorage.getItem("permissions")
			if (permissions == null || permissions === "") {
				next({
					name: 'Login'
				})
			}
		}
	}
	return next()
})

export default router
