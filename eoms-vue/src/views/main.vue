<template>
	<div
		class="site-wrapper"
		:class="{ 'site-sidebar--fold': sidebarFold }"
		v-loading.fullscreen.lock="loading"
		element-loading-text="拼命加载中"
	>
		<!--nav导航区域-->
		<nav class="site-navbar" :class="'site-navbar--' + navbarLayoutType">
			<div class="site-navbar__header">
				<h1 class="site-navbar__brand">
					<a class="site-navbar__brand-lg">企业办公管理系统</a>
					<a class="site-navbar__brand-mini">OA</a>
				</h1>
			</div>
			<div class="site-navbar__body clearfix">
				<!--展开按钮-->
				<el-menu class="site-navbar__menu" mode="horizontal">
					<el-menu-item class="site-navbar__switch" index="0" @click="handleSwitch">
						<SvgIcon name="zhedie" class="icon-svg" />
					</el-menu-item>
				</el-menu>
				<el-menu class="site-navbar__menu site-navbar__menu--right" mode="horizontal">
					<!--邮件按钮-->
					<!--<el-menu-item index="1" class="site-navbar__switch">
						<template #title>
							<el-badge value="0">
								<SvgIcon name="duanxin" class="icon-svg duanxin-svg" />
							</el-badge>
						</template>
					</el-menu-item>-->
					<!--右上角头像按钮-->
					<el-menu-item class="site-navbar__avatar" index="3">
						<el-dropdown>
							<span class="el-dropdown-link">
								<img :src="photo" />
								{{ name }}
							</span>
							<template #dropdown>
								<el-dropdown-menu>
									<!--TODO-->
									<el-dropdown-item @click="$router.push({name: 'UserInfo'})">
										个人中心
									</el-dropdown-item>
									<el-dropdown-item @click="updatePasswordHandle()">
										修改密码
									</el-dropdown-item>
									<el-dropdown-item @click="logout">退出</el-dropdown-item>
								</el-dropdown-menu>
							</template>
						</el-dropdown>
					</el-menu-item>
				</el-menu>
			</div>
			<update-password v-if="updatePasswordVisible" ref="updatePassword"></update-password>
		</nav>
		<!--左边sidebar-->
		<aside class="site-sidebar site-sidebar--dark">
			<div class="site-sidebar__inner">
				<el-menu
					:default-active="menuActiveName || 'home'"
					:collapse="sidebarFold"
					:collapseTransition="false"
					class="site-sidebar__menu"
				>
					<!--首页-->
					<el-menu-item index="home" @click="$router.push({ name: 'Home' })">
						<SvgIcon name="home" class="icon-svg" />
						<span slot="title">首页</span>
					</el-menu-item>
					<!--信息中心-->
					<el-submenu
							index="信息中心"
							:popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
					>
						<template #title>
							<svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-ba633cb8="" class="icon-svg-2">
								<path fill="currentColor" d="m795.904 750.72 124.992 124.928a32 32 0 0 1-45.248 45.248L750.656 795.904a416 416 0 1 1 45.248-45.248zM480 832a352 352 0 1 0 0-704 352 352 0 0 0 0 704z">
								</path>
							</svg>
							<span slot="title">信息中心</span>
						</template>
						<el-menu-item
								index="personalInfo"
								@click="$router.push({ name: 'UserInfo' })"
						>
							<svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-ba633cb8="" class="icon-svg-2">
								<path fill="currentColor" d="M628.736 528.896A416 416 0 0 1 928 928H96a415.872 415.872 0 0 1 299.264-399.104L512 704l116.736-175.104zM720 304a208 208 0 1 1-416 0 208 208 0 0 1 416 0z">
								</path>
							</svg>
							<span slot="title">个人中心</span>
						</el-menu-item>
						<el-menu-item
								index="mailList"
								@click="$router.push({ name: 'MailList' })"
						>
							<svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-ba633cb8="" class="icon-svg-2">
								<path fill="currentColor" d="M79.36 432.256 591.744 944.64a32 32 0 0 0 35.2 6.784l253.44-108.544a32 32 0 0 0 9.984-52.032l-153.856-153.92a32 32 0 0 0-36.928-6.016l-69.888 34.944L358.08 394.24l35.008-69.888a32 32 0 0 0-5.952-36.928L233.152 133.568a32 32 0 0 0-52.032 10.048L72.512 397.056a32 32 0 0 0 6.784 35.2zm60.48-29.952 81.536-190.08L325.568 316.48l-24.64 49.216-20.608 41.216 32.576 32.64 271.552 271.552 32.64 32.64 41.216-20.672 49.28-24.576 104.192 104.128-190.08 81.472L139.84 402.304zM512 320v-64a256 256 0 0 1 256 256h-64a192 192 0 0 0-192-192zm0-192V64a448 448 0 0 1 448 448h-64a384 384 0 0 0-384-384z">
								</path>
							</svg>
							<span slot="title">企业通讯录</span>
						</el-menu-item>
					</el-submenu>
					<!--今日事项-->
					<el-submenu
							index="今日事项"
							:popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
					>
						<template #title>
							<svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-ba633cb8="" class="icon-svg-2">
								<path fill="currentColor" d="M192 128v768h640V128H192zm-32-64h704a32 32 0 0 1 32 32v832a32 32 0 0 1-32 32H160a32 32 0 0 1-32-32V96a32 32 0 0 1 32-32z"></path>
								<path fill="currentColor" d="M672 128h64v768h-64zM96 192h128q32 0 32 32t-32 32H96q-32 0-32-32t32-32zm0 192h128q32 0 32 32t-32 32H96q-32 0-32-32t32-32zm0 192h128q32 0 32 32t-32 32H96q-32 0-32-32t32-32zm0 192h128q32 0 32 32t-32 32H96q-32 0-32-32t32-32z"></path>
							</svg>
							<span slot="title">今日事项</span>
						</template>
						<el-menu-item
								index="checkin"
								@click="$router.push({ name: 'Checkin' })"
						>
							<svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-ba633cb8="" class="icon-svg-2">
								<path fill="currentColor" d="M512 896a384 384 0 1 0 0-768 384 384 0 0 0 0 768zm0 64a448 448 0 1 1 0-896 448 448 0 0 1 0 896z"></path>
								<path fill="currentColor" d="M745.344 361.344a32 32 0 0 1 45.312 45.312l-288 288a32 32 0 0 1-45.312 0l-160-160a32 32 0 1 1 45.312-45.312L480 626.752l265.344-265.408z"></path>
							</svg>
							<span slot="title">在线签到</span>
						</el-menu-item>
						<el-menu-item index="todo" @click="$router.push({ name: 'Todo' })">
							<svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-ba633cb8="" class="icon-svg-2">
								<path fill="currentColor" d="M704 192h160v736H160V192h160.064v64H704v-64zM311.616 537.28l-45.312 45.248L447.36 763.52l316.8-316.8-45.312-45.184L447.36 673.024 311.616 537.28zM384 192V96h256v96H384z">
								</path>
							</svg>
							<span slot="title">我的待办</span>
						</el-menu-item>
					</el-submenu>
					<!--在线办公-->
					<el-submenu
						index="在线办公"
						:popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
					>
						<template #title>
							<SvgIcon name="meeting_fill" class="icon-svg" />
							<span slot="title">在线办公</span>
						</template>
						<el-menu-item
							index="approval"
							v-if="isAuth(['ROOT', 'WORKFLOW:APPROVAL', 'FILE:ARCHIVE'])"
							@click="$router.push({ name: 'Approval' })"
						>
							<svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-ba633cb8="" class="icon-svg-2">
								<path fill="currentColor" d="M805.504 320 640 154.496V320h165.504zM832 384H576V128H192v768h640V384zM160 64h480l256 256v608a32 32 0 0 1-32 32H160a32 32 0 0 1-32-32V96a32 32 0 0 1 32-32zm318.4 582.144 180.992-180.992L704.64 510.4 478.4 736.64 320 578.304l45.248-45.312L478.4 646.144z">
								</path>
							</svg>
							<span slot="title">审批任务</span>
						</el-menu-item>
						<el-menu-item index="leave" @click="$router.push({ name: 'Leave' })">
							<svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-ba633cb8="" class="icon-svg-2">
								<path fill="currentColor" d="M192 256a64 64 0 0 0-64 64v448a64 64 0 0 0 64 64h640a64 64 0 0 0 64-64V320a64 64 0 0 0-64-64H192zm0-64h640a128 128 0 0 1 128 128v448a128 128 0 0 1-128 128H192A128 128 0 0 1 64 768V320a128 128 0 0 1 128-128z"></path>
								<path fill="currentColor" d="M544 512h96a32 32 0 0 1 0 64h-96v96a32 32 0 0 1-64 0v-96h-96a32 32 0 0 1 0-64h96v-96a32 32 0 0 1 64 0v96zM352 128v64h320v-64H352zm-32-64h384a32 32 0 0 1 32 32v128a32 32 0 0 1-32 32H320a32 32 0 0 1-32-32V96a32 32 0 0 1 32-32z"></path>
							</svg>
							<span slot="title">员工请假</span>
						</el-menu-item>
						<el-menu-item index="amect" @click="$router.push({ name: 'Amect' })">
							<svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-ba633cb8="" class="icon-svg-2">
								<path fill="currentColor" d="m466.752 512-90.496-90.496a32 32 0 0 1 45.248-45.248L512 466.752l90.496-90.496a32 32 0 1 1 45.248 45.248L557.248 512l90.496 90.496a32 32 0 1 1-45.248 45.248L512 557.248l-90.496 90.496a32 32 0 0 1-45.248-45.248L466.752 512z"></path>
								<path fill="currentColor" d="M512 896a384 384 0 1 0 0-768 384 384 0 0 0 0 768zm0 64a448 448 0 1 1 0-896 448 448 0 0 1 0 896z"></path>
							</svg>
							<span slot="title">违纪罚款</span>
						</el-menu-item>
						<el-menu-item index="reim" @click="$router.push({ name: 'Reim' })">
							<svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-ba633cb8="" class="icon-svg-2">
								<path fill="currentColor" d="M256 640v192h640V384H768v-64h150.976c14.272 0 19.456 1.472 24.64 4.288a29.056 29.056 0 0 1 12.16 12.096c2.752 5.184 4.224 10.368 4.224 24.64v493.952c0 14.272-1.472 19.456-4.288 24.64a29.056 29.056 0 0 1-12.096 12.16c-5.184 2.752-10.368 4.224-24.64 4.224H233.024c-14.272 0-19.456-1.472-24.64-4.288a29.056 29.056 0 0 1-12.16-12.096c-2.688-5.184-4.224-10.368-4.224-24.576V640h64z"></path>
								<path fill="currentColor" d="M768 192H128v448h640V192zm64-22.976v493.952c0 14.272-1.472 19.456-4.288 24.64a29.056 29.056 0 0 1-12.096 12.16c-5.184 2.752-10.368 4.224-24.64 4.224H105.024c-14.272 0-19.456-1.472-24.64-4.288a29.056 29.056 0 0 1-12.16-12.096C65.536 682.432 64 677.248 64 663.04V169.024c0-14.272 1.472-19.456 4.288-24.64a29.056 29.056 0 0 1 12.096-12.16C85.568 129.536 90.752 128 104.96 128h685.952c14.272 0 19.456 1.472 24.64 4.288a29.056 29.056 0 0 1 12.16 12.096c2.752 5.184 4.224 10.368 4.224 24.64z"></path>
								<path fill="currentColor" d="M448 576a160 160 0 1 1 0-320 160 160 0 0 1 0 320zm0-64a96 96 0 1 0 0-192 96 96 0 0 0 0 192z"></path>
							</svg>
							<span slot="title">报销申报</span>
						</el-menu-item>
					</el-submenu>
					<!--会议管理-->
					<el-submenu
						index="会议管理"
						:popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
					>
						<template #title>
							<svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-ba633cb8="" class="icon-svg-2">
								<path fill="currentColor" d="M736 504a56 56 0 1 1 0-112 56 56 0 0 1 0 112zm-224 0a56 56 0 1 1 0-112 56 56 0 0 1 0 112zm-224 0a56 56 0 1 1 0-112 56 56 0 0 1 0 112zM128 128v640h192v160l224-160h352V128H128z">
								</path>
							</svg>
							<span slot="title">会议管理</span>
						</template>
						<el-menu-item
							index="meeting-room"
							@click="$router.push({ name: 'MeetingRoom' })"
						>
							<SvgIcon name="warehouse_fill" class="icon-svg" />
							<span slot="title">会议室</span>
						</el-menu-item>
						<el-menu-item
								index="meeting"
								@click="$router.push({ name: 'Meeting' })"
						>
							<SvgIcon name="warehouse_fill" class="icon-svg" />
							<span slot="title">会议列表</span>
						</el-menu-item>
						<el-menu-item
							index="demo-echarts"
							@click="$router.push({ name: 'OfflineMeeting' })"
						>
							<SvgIcon name="trust_fill" class="icon-svg" />
							<span slot="title">线下会议</span>
						</el-menu-item>
						<el-menu-item
							index="demo-echarts"
							@click="$router.push({ name: 'OnlineMeeting' })"
						>
							<SvgIcon name="service_fill" class="icon-svg" />
							<span slot="title">线上会议</span>
						</el-menu-item>
					</el-submenu>
					<!--组织管理-->
					<el-submenu
							index="组织管理"
							:popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
							v-if="isAuth(['ROOT', 'USER:SELECT'])"
					>
						<template #title>
							<SvgIcon name="users_fill" class="icon-svg" />
							<span slot="title">组织管理</span>
						</template>
						<el-menu-item
								index="user"
								v-if="isAuth(['ROOT', 'USER:SELECT'])"
								@click="$router.push({ name: 'User' })"
						>
							<SvgIcon name="user_fill" class="icon-svg" />
							<span slot="title">用户管理</span>
						</el-menu-item>
						<el-menu-item
								index="role"
								v-if="isAuth(['ROOT'])"
								@click="$router.push({ name: 'Role' })"
						>
							<SvgIcon name="role_fill" class="icon-svg" />
							<span slot="title">角色管理</span>
						</el-menu-item>
						<el-menu-item
								index="dept"
								v-if="isAuth(['ROOT'])"
								@click="$router.push({ name: 'Dept' })"
								ref="ABC"
						>
							<SvgIcon name="company_fill" class="icon-svg" />
							<span slot="title">部门管理</span>
						</el-menu-item>
					</el-submenu>
					<!--系统设置-->
					<el-submenu
						index="系统设置"
						:popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
						v-if="isAuth(['ROOT'])"
					>
						<template #title>
							<svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-ba633cb8="" class="icon-svg-2">
								<path fill="currentColor" d="M600.704 64a32 32 0 0 1 30.464 22.208l35.2 109.376c14.784 7.232 28.928 15.36 42.432 24.512l112.384-24.192a32 32 0 0 1 34.432 15.36L944.32 364.8a32 32 0 0 1-4.032 37.504l-77.12 85.12a357.12 357.12 0 0 1 0 49.024l77.12 85.248a32 32 0 0 1 4.032 37.504l-88.704 153.6a32 32 0 0 1-34.432 15.296L708.8 803.904c-13.44 9.088-27.648 17.28-42.368 24.512l-35.264 109.376A32 32 0 0 1 600.704 960H423.296a32 32 0 0 1-30.464-22.208L357.696 828.48a351.616 351.616 0 0 1-42.56-24.64l-112.32 24.256a32 32 0 0 1-34.432-15.36L79.68 659.2a32 32 0 0 1 4.032-37.504l77.12-85.248a357.12 357.12 0 0 1 0-48.896l-77.12-85.248A32 32 0 0 1 79.68 364.8l88.704-153.6a32 32 0 0 1 34.432-15.296l112.32 24.256c13.568-9.152 27.776-17.408 42.56-24.64l35.2-109.312A32 32 0 0 1 423.232 64H600.64zm-23.424 64H446.72l-36.352 113.088-24.512 11.968a294.113 294.113 0 0 0-34.816 20.096l-22.656 15.36-116.224-25.088-65.28 113.152 79.68 88.192-1.92 27.136a293.12 293.12 0 0 0 0 40.192l1.92 27.136-79.808 88.192 65.344 113.152 116.224-25.024 22.656 15.296a294.113 294.113 0 0 0 34.816 20.096l24.512 11.968L446.72 896h130.688l36.48-113.152 24.448-11.904a288.282 288.282 0 0 0 34.752-20.096l22.592-15.296 116.288 25.024 65.28-113.152-79.744-88.192 1.92-27.136a293.12 293.12 0 0 0 0-40.256l-1.92-27.136 79.808-88.128-65.344-113.152-116.288 24.96-22.592-15.232a287.616 287.616 0 0 0-34.752-20.096l-24.448-11.904L577.344 128zM512 320a192 192 0 1 1 0 384 192 192 0 0 1 0-384zm0 64a128 128 0 1 0 0 256 128 128 0 0 0 0-256z"></path>
							</svg>
							<span slot="title">系统设置</span>
						</template>
						<el-menu-item
							index="amect-type"
							@click="$router.push({ name: 'AmectType' })"
						>
							<SvgIcon name="tool_fill" class="icon-svg" />
							<span slot="title">罚款类型</span>
						</el-menu-item>
						<el-menu-item
								index="amect-type"
								@click="$router.push({ name: 'Notice' })"
						>
							<svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-ba633cb8="" class="icon-svg-2">
								<path fill="currentColor" d="m174.72 855.68 135.296-45.12 23.68 11.84C388.096 849.536 448.576 864 512 864c211.84 0 384-166.784 384-352S723.84 160 512 160 128 326.784 128 512c0 69.12 24.96 139.264 70.848 199.232l22.08 28.8-46.272 115.584zm-45.248 82.56A32 32 0 0 1 89.6 896l58.368-145.92C94.72 680.32 64 596.864 64 512 64 299.904 256 96 512 96s448 203.904 448 416-192 416-448 416a461.056 461.056 0 0 1-206.912-48.384l-175.616 58.56z"></path>
								<path fill="currentColor" d="M512 563.2a51.2 51.2 0 1 1 0-102.4 51.2 51.2 0 0 1 0 102.4zm192 0a51.2 51.2 0 1 1 0-102.4 51.2 51.2 0 0 1 0 102.4zm-384 0a51.2 51.2 0 1 1 0-102.4 51.2 51.2 0 0 1 0 102.4z"></path>
							</svg>
							<span slot="title">公告管理</span>
						</el-menu-item>
					</el-submenu>
				</el-menu>
			</div>
		</aside>
		<div class="site-content__wrapper" :style="{ 'min-height': documentClientHeight + 'px' }">
			<main class="site-content" :class="{ 'site-content--tabs': $route.meta.isTab }">
				<el-tabs
					v-if="$route.meta.isTab"
					v-model="mainTabsActiveName"
					:closable="true"
					@tab-click="selectedTabHandle"
					@tab-remove="removeTabHandle"
				>
					<el-dropdown class="site-tabs__tools" trigger="click">
						<i class="el-icon-arrow-down el-icon--right"></i>
						<template #dropdown>
							<el-dropdown-menu slot="dropdown">
								<el-dropdown-item @click.native="tabsCloseCurrentHandle">
									关闭当前标签页
								</el-dropdown-item>
								<el-dropdown-item @click.native="tabsCloseOtherHandle">
									关闭其它标签页
								</el-dropdown-item>
								<el-dropdown-item @click.native="tabsCloseAllHandle">
									关闭全部标签页
								</el-dropdown-item>
							</el-dropdown-menu>
						</template>
					</el-dropdown>
					<el-tab-pane v-for="item in mainTabs" :label="item.title" :name="item.name">
						<el-card :body-style="siteContentViewHeight">
							<iframe
								v-if="item.type === 'iframe'"
								:src="item.iframeUrl"
								width="100%"
								height="100%"
								frameborder="0"
								scrolling="yes"
							></iframe>
							<router-view v-if="item.name === mainTabsActiveName" />
						</el-card>
					</el-tab-pane>
				</el-tabs>
				<!-- 主入口标签页 e -->
				<el-card v-else :body-style="siteContentViewHeight"><router-view /></el-card>
			</main>
		</div>
	</div>
</template>

<script>

import SvgIcon from '../components/SvgIcon.vue';
import { isURL } from '../utils/verify';
import UpdatePassword from './user/update-password.vue';
import { ref, provide } from 'vue';

export default {
	components: {
		SvgIcon,
		UpdatePassword
	},
	data: function() {
		return {
			navbarLayoutType: '',
			sidebarFold: false,
			sidebarLayoutSkin: 'dark',
			name: '',
			photo: '',
			documentClientHeight: 0,
			siteContentViewHeight: {},
			height: null,
			mainTabs: [],
			mainTabsActiveName: '',
			menuActiveName: '',
			updatePasswordVisible: false
		};
	},
	created() {
		let that = this;
		that.routeHandle(that.$route);
		that.$options.sockets.onopen = function(resp) {
			//发送心跳检测，避免超时后服务端切断连接
			setInterval(function() {
				that.$socket.sendObj({ opt: 'ping' });
			}, 60 * 1000);
		};
	},
	watch: {
		$route: {
			handler(to, from) {
				if (to.path != from.path) {
					// this.$router.push(to);
					this.routeHandle(to);
				}
			}
		}
	},
	methods: {
		logout: function() {
			let that = this
			that.$http("/user/logout", "GET", null, true, function(resp) {
				localStorage.removeItem("permissions")
				that.$router.push({name: "Login"})
			})
		},
		updatePasswordHandle: function() {
			this.updatePasswordVisible = true
			this.$nextTick(() => {
				this.$refs.updatePassword.init()
			})
		},
		handleSwitch: function() {
			if (this.sidebarFold) {
				this.navbarLayoutType = '';
			} else {
				this.navbarLayoutType = 'fold';
			}
			this.sidebarFold = !this.sidebarFold;
		},
		resetDocumentClientHeight: function() {
			this.documentClientHeight = document.documentElement['clientHeight'];
			window.onresize = () => {
				this.documentClientHeight = document.documentElement['clientHeight'];
				this.loadSiteContentViewHeight();
			};
		},
		loadSiteContentViewHeight: function() {
			let height = this.documentClientHeight - 50 - 30 - 2;
			if (this.$route.meta.isTab) {
				height -= 40;
				this.siteContentViewHeight = isURL(this.$route.meta.iframeUrl)
					? { height: height + 'px' }
					: { minHeight: height + 'px' };
				this.height=provide('height',{ height: height-40 + 'px' })
			}
			this.siteContentViewHeight = { minHeight: height + 'px'};
			
		},
		routeHandle: function(route) {
			//每次切换页面，重新计算页面高度和内容区高度
			this.resetDocumentClientHeight();
			this.loadSiteContentViewHeight();

			if (route.meta.isTab) {
				// tab选中, 不存在先添加
				var tab = this.mainTabs.filter(item => item.name === route.name)[0];
				if (!tab) {
					if (route.meta.isDynamic) {
						route = this.dynamicMenuRoutes.filter(item => item.name === route.name)[0];

						if (!route) {
							return console.error('未能找到可用标签页!');
						}
					}
					tab = {
						menuId: route.meta.menuId || route.name,
						name: route.name,
						title: route.meta.title,
						type: isURL(route.meta.iframeUrl) ? 'iframe' : 'module',
						iframeUrl: route.meta.iframeUrl || '',
						params: route.params,
						query: route.query
					};
					this.mainTabs = this.mainTabs.concat(tab);
				}
				this.menuActiveName = tab.menuId + '';
				this.mainTabsActiveName = tab.name;
			}
		},

		selectedTabHandle: function(tab, e) {
			tab = this.mainTabs.filter(item => item.name === tab.paneName);
			if (tab.length >= 1) {
				this.$router.push({
					name: tab[0].name,
					query: tab[0].query,
					params: tab[0].params
				});
			}
		},
		removeTabHandle: function(tabName) {
			this.mainTabs = this.mainTabs.filter(item => item.name !== tabName);
			if (this.mainTabs.length >= 1) {
				// 当前选中tab被删除
				if (tabName === this.mainTabsActiveName) {
					var tab = this.mainTabs[this.mainTabs.length - 1];
					this.$router.push(
						{ name: tab.name, query: tab.query, params: tab.params },
						() => {
							this.mainTabsActiveName = this.$route.name;
						}
					);
				}
			} else {
				this.menuActiveName = '';
				this.$router.push({ name: 'Home' });
			}
		},
		// tabs, 关闭当前
		tabsCloseCurrentHandle: function() {
			this.removeTabHandle(this.mainTabsActiveName);
		},
		// tabs, 关闭其它
		tabsCloseOtherHandle: function() {
			this.mainTabs = this.mainTabs.filter(item => item.name === this.mainTabsActiveName);
		},
		// tabs, 关闭全部
		tabsCloseAllHandle: function() {
			this.mainTabs = [];
			this.menuActiveName = '';
			this.$router.push({ name: 'Home' });
		}
	},
	mounted: function() {
		let that = this;
		//加载用户数据
		that.$http('user/loadUserInfo', 'GET', null, true, function(resp) {
			let json = resp;
			let name = json.name;
			let photo = json.photo;
			that.name = name;
			that.photo = photo;
		});

		that.resetDocumentClientHeight();
		that.loadSiteContentViewHeight();
	}
};
</script>

<style lang="scss">
	@import '../assets/scss/index.scss';
</style>
