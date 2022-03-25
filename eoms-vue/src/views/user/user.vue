<template>
	<div class="app-container" v-if="isAuth(['ROOT', 'USER:SELECT'])">
		<el-form :model="dataForm" ref="dataForm" :inline="true" :rules="dataRule" label-width="63px">
			<el-form-item label="部门"  prop="deptId" v-if="isAuth(['ROOT'])">
				<el-select
						v-model="dataForm.deptId"
						class="input"
						placeholder="部门"
						size="small"
						clearable="clearable"
				>
					<el-option v-for="one in deptList" :label="one.deptName" :value="one.id" />
				</el-select>
			</el-form-item>
			<el-form-item label="姓名" prop="name">
				<el-input
						v-model="dataForm.name"
						placeholder="姓名"
						clearable
						size="small"
						@keyup.enter.native="searchHandle"
				/>
			</el-form-item>
			<el-form-item label="性别" prop="sex">
				<el-select v-model="dataForm.sex" class="input" placeholder="性别" size="small" clearable="clearable">
					<el-option label="男" value="男" />
					<el-option label="女" value="女" />
				</el-select>
			</el-form-item>
			<el-form-item label="角色" prop="role" v-if="isAuth(['ROOT'])">
				<el-select v-model="dataForm.role" class="input" placeholder="角色" size="small" clearable="clearable">
					<el-option v-for="one in roleList" :label="one.roleName" :value="one.roleName" />
				</el-select>
			</el-form-item>
			<el-form-item label="状态" prop="status">
				<el-select
						v-model="dataForm.status"
						class="input"
						placeholder="状态"
						size="small"
						clearable="clearable"
				>
					<el-option label="在职" value="1" />
					<el-option label="离职" value="2" />
				</el-select>
			</el-form-item>
		</el-form>

		<el-row :gutter="15" class="mb8" style="margin-bottom: 10px">
			<el-col :span="1.5">
				<el-button
						type="primary"
						plain
						icon="el-icon-plus"
						size="mini"
						@click="addHandle"
				>新增</el-button>
			</el-col>
			<el-col :span="1.5">
				<el-button
						type="success"
						plain
						icon="el-icon-edit"
						size="mini"
						:disabled="single"
						@click="updateHandle(dataListSelections.map(item => item.id)[0])"
				>修改</el-button>
			</el-col>
			<el-col :span="1.5">
				<el-button
						type="danger"
						plain
						icon="el-icon-delete"
						size="mini"
						:disabled="multiple"
						@click="deleteHandle()"
				>删除</el-button>
			</el-col>
			<el-col :span="1.5">
				<el-button
						type="info"
						plain
						icon="el-icon-download"
						size="mini"
						:disabled="multiple"
						@click="deleteHandle()"
				>导出</el-button>
				<el-button style="margin-left: 700px" type="primary" icon="el-icon-search" size="mini" @click="searchHandle">搜索</el-button>
				<el-button icon="el-icon-refresh" size="mini" @click="resetForm">重置</el-button>
			</el-col>
		</el-row>

		<el-table v-loading="dataListLoading" :data="dataList" @selection-change="selectionChangeHandle" :header-cell-style="{background:'#eef1f6',color:'#606266'}">
			<el-table-column :selectable="selectable" type="selection" width="55" align="center" />
			<el-table-column type="index" header-align="center" align="center" min-width="50px" label="序号">
				<template #default="scope">
					<span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
				</template>
			</el-table-column>
			<el-table-column label="姓名" min-width="100px" align="center" prop="name" :show-overflow-tooltip="true"/>
			<el-table-column label="性别" align="center" prop="sex" min-width="80px">
				<template #default="scope">
					<el-tag type="primary" v-if="scope.row.sex === '男'">{{scope.row.sex}}</el-tag>
					<el-tag type="success" v-if="scope.row.sex === '女'">{{scope.row.sex}}</el-tag>
				</template>
			</el-table-column>
			<el-table-column prop="tel" header-align="center" align="center" min-width="130px" label="电话" />
			<el-table-column label="入职日期" align="center" prop="hiredate" min-width="120px"></el-table-column>
			<el-table-column prop="roles" header-align="center" align="center" min-width="200px" label="角色" :show-overflow-tooltip="true"/>
			<el-table-column prop="deptName" header-align="center" align="center" min-width="100px" label="部门" />
			<el-table-column prop="status" header-align="center" align="center" min-width="80px" label="状态" />
			<el-table-column label="操作" align="center" min-width="150px" class-name="small-padding fixed-width">
				<template #default="scope">
					<el-button
							:disabled="scope.row.root"
							size="medium"
							type="text"
							icon="el-icon-edit"
							@click="updateHandle(scope.row.id)"
					>修改</el-button>
					<el-button
							:disabled="scope.row.root"
							size="medium"
							type="text"
							icon="el-icon-delete"
							@click="deleteHandle(scope.row.id)"
					>删除</el-button>
				</template>
			</el-table-column>
		</el-table>

		<el-pagination
				@size-change="sizeChangeHandle"
				@current-change="currentChangeHandle"
				:current-page="pageIndex"
				:page-sizes="[5, 10, 20]"
				:page-size="pageSize"
				:total="totalCount"
				layout="total, sizes, prev, pager, next, jumper"
		></el-pagination>
		<add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="loadDataList"></add-or-update>
	</div>
</template>

<script>
import AddOrUpdate from './user-add-or-update.vue';
export default {
	components: {
		AddOrUpdate,
	},
	data() {
		return {
			dataForm: {
				name: '',
				sex: '',
				role: '',
				deptId: '',
				status: ''
			},
			dataList: [],
			roleList: [],
			deptList: [],
			pageIndex: 1,
			pageSize: 5,
			totalCount: 0,
			dataListLoading: false,
			// 非单个禁用
			single: true,
			// 非多个禁用
			multiple: true,
			dataListSelections: [],
			addOrUpdateVisible: false,
			dataRule: {
				name: [{ required: false, pattern: '^[\u4e00-\u9fa5]{1,10}$', message: '姓名格式错误' }]
			}
		};
	},
	methods: {
		// 查询用户列表
		loadDataList: function() {
			let that = this
			that.dataListLoading = true
			let data = {
				page: that.pageIndex,
				length: that.pageSize,
				name: that.dataForm.name,
				sex: that.dataForm.sex,
				role: that.dataForm.role,
				deptId: that.dataForm.deptId,
				status: that.dataForm.status
			}
			that.$http("/user/searchUserByPage", "POST", data, true, function (resp) {
				let page = resp.page
				let list = page.list
				for (let one of list) {
					if (one.status === 1) {
						one.status = "在职"
					} else if (one.status === 2) {
						one.status = "离职"
					}
				}
				that.dataList = list
				that.totalCount = page.totalCount
				that.dataListLoading = false
			})
		},
		// 加载所有角色信息
		loadRoleList: function() {
			let that = this;
			that.$http('/role/searchAllRoles', 'GET', null, true, function(resp) {
				that.roleList = resp.roles
			});
		},
		// 加载所有部门信息
		loadDeptList: function() {
			let that = this;
			that.$http('/dept/searchAllDept', 'GET', null, true, function(resp) {
				that.deptList = resp.deptList
			});
		},
		selectionChangeHandle: function(val) {
			this.dataListSelections = val
			this.single = val.length !== 1
			this.multiple = !val.length
		},
		// 每页多少数据
		sizeChangeHandle: function (val) {
			this.pageSize = val
			this.pageIndex = 1
			this.loadDataList()
		},
		// 页数变化
		currentChangeHandle: function (val) {
			this.pageIndex = val
			this.loadDataList()
		},
		// 条件查询
		searchHandle: function () {
			//先执行表单验证
			this.$refs['dataForm'].validate(valid => {
				if (valid) {
					//清理页面上的表单验证结果
					this.$refs['dataForm'].clearValidate();
					//不允许上传空字符串给后端，但是可以传null值
					if (this.dataForm.name === '') {
						this.dataForm.name = null;
					}
					//如果当前页面不是第一页，则跳转到第一页显示查询的结果
					if (this.pageIndex !== 1) {
						this.pageIndex = 1;
					}
					this.loadDataList();
				} else {
					return false;
				}
			});
		},
		// 重置表单
		resetForm: function () {
			this.dataForm = {}
			this.loadDataList()
		},
		// 添加用户
		addHandle: function () {
			this.addOrUpdateVisible = true
			this.$nextTick(() => {
				this.$refs.addOrUpdate.init();
			});
		},
		// 更新
		updateHandle: function (id) {
			this.addOrUpdateVisible = true
			this.$nextTick(() => {
				this.$refs.addOrUpdate.init(id);
			})
		},
		// 删除
		deleteHandle: function (id) {
			let that = this
			let ids = id ? [id] : that.dataListSelections.map(item => item.id)
			if (ids.length === 0) {
				that.$message({
					message: '没有选中记录',
					type: 'warning',
					duration: 1200
				});
			} else {
				that.$confirm(`确定要删除选中的记录？`, '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning'
				}).then(() => {
					that.$http("/user/deleteUserByIds", "POST", {ids: ids}, true, function (resp) {
						if (resp.rows > 0) {
							that.$message({
								message: '删除成功',
								type: 'success',
								duration: 1200
							});
							that.loadDataList();
						} else {
							that.$message({
								message: '删除失败',
								type: 'error',
								duration: 1200
							});
						}
					})
				})
			}
		},
		// 多选框能不能勾选
		selectable: function(row) {
			return !(row.root);
		}
	},
	created: function() {
		this.loadDataList()
		this.loadRoleList()
		this.loadDeptList()
	}
};
</script>

<style lang="less" scoped="scoped"></style>
