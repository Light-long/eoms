<template>
	<div
		class="site-wrapper"
		:class="{ 'site-sidebar--fold': sidebarFold }"
		v-loading.fullscreen.lock="loading"
		element-loading-text="拼命加载中"
	>
		<nav class="site-navbar" :class="'site-navbar--' + navbarLayoutType">
			<div class="site-navbar__header">
				<h1 class="site-navbar__brand">
					<a class="site-navbar__brand-lg">EOMS办公管理系统</a>
					<a class="site-navbar__brand-mini">EOMS</a>
				</h1>
			</div>
			<div class="site-navbar__body clearfix">
				<el-menu class="site-navbar__menu" mode="horizontal">
					<el-menu-item class="site-navbar__switch" index="0" @click="handleSwitch">
						<SvgIcon name="zhedie" class="icon-svg" />
					</el-menu-item>
				</el-menu>
				<el-menu class="site-navbar__menu site-navbar__menu--right" mode="horizontal">
					<el-menu-item index="1" class="site-navbar__switch">
						<template #title>
							<el-badge value="0">
								<SvgIcon name="duanxin" class="icon-svg duanxin-svg" />
							</el-badge>
						</template>
					</el-menu-item>
					<el-menu-item class="site-navbar__avatar" index="3">
						<el-dropdown>
							<span class="el-dropdown-link">
								<img :src="photo" />
								{{ name }}
							</span>
							<template #dropdown>
								<el-dropdown-menu>
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
		<aside class="site-sidebar site-sidebar--dark">
			<div class="site-sidebar__inner">
				<el-menu
					:default-active="menuActiveName || 'home'"
					:collapse="sidebarFold"
					:collapseTransition="false"
					class="site-sidebar__menu"
				>
					<el-menu-item index="home" @click="$router.push({ name: 'Home' })">
						<SvgIcon name="home" class="icon-svg" />
						<span slot="title">首页</span>
					</el-menu-item>
					<el-submenu
						index="组织管理"
						:popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
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
							v-if="isAuth(['ROOT', 'ROLE:SELECT'])"
							@click="$router.push({ name: 'Role' })"
						>
							<SvgIcon name="role_fill" class="icon-svg" />
							<span slot="title">角色管理</span>
						</el-menu-item>
						<el-menu-item
								index="dept"
								v-if="isAuth(['ROOT', 'DEPT:SELECT'])"
								@click="$router.push({ name: 'Dept' })"
								ref="ABC"
						>
							<SvgIcon name="company_fill" class="icon-svg" />
							<span slot="title">部门管理</span>
						</el-menu-item>
					</el-submenu>
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
							<SvgIcon name="warehouse_fill" class="icon-svg" />
							<span slot="title">审批任务</span>
						</el-menu-item>
						<el-menu-item index="leave" @click="$router.push({ name: 'Leave' })">
							<SvgIcon name="night_fill" class="icon-svg" />
							<span slot="title">员工请假</span>
						</el-menu-item>
						<el-menu-item index="amect" @click="$router.push({ name: 'Amect' })">
							<SvgIcon name="cry_fill" class="icon-svg" />
							<span slot="title">违纪罚款</span>
						</el-menu-item>
						<el-menu-item index="reim" @click="$router.push({ name: 'Reim' })">
							<SvgIcon name="assurance_fill" class="icon-svg" />
							<span slot="title">报销管理</span>
						</el-menu-item>
						<el-menu-item index="todo" @click="$router.push({ name: 'Todo' })">
							<SvgIcon name="assurance_fill" class="icon-svg" />
							<span slot="title">我的待办</span>
						</el-menu-item>
					</el-submenu>

					<el-submenu
						index="会议管理"
						:popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
					>
						<template #title>
							<SvgIcon name="meeting_fill" class="icon-svg" />
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
					<el-submenu
						index="系统设置"
						:popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
						v-if="isAuth(['ROOT'])"
					>
						<template #title>
							<SvgIcon name="system_fill" class="icon-svg" />
							<span slot="title">系统设置</span>
						</template>
						<el-menu-item
							index="amect-type"
							@click="$router.push({ name: 'AmectType' })"
						>
							<SvgIcon name="tool_fill" class="icon-svg" />
							<span slot="title">罚款类型</span>
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
import { isURL } from '../utils/validate';
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
