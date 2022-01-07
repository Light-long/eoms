<template>
	<div>
		<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
			<el-form-item prop="name">
				<el-input
					v-model="dataForm.name"
					placeholder="姓名"
					size="medium"
					class="input"
					clearable="clearable"
				/>
			</el-form-item>
			<el-form-item>
				<el-select
					v-model="dataForm.deptId"
					class="input"
					placeholder="部门"
					size="medium"
					clearable="clearable"
				>
					<el-option v-for="one in deptList" :label="one.deptName" :value="one.id" />
				</el-select>
			</el-form-item>
			<el-form-item>
				<el-select
					v-model="dataForm.typeId"
					class="input"
					placeholder="罚款类型"
					size="medium"
					clearable="clearable"
				>
					<el-option v-for="one in amectTypeList" :label="one.type" :value="one.id" />
				</el-select>
			</el-form-item>
			<el-form-item>
				<el-date-picker
					v-model="dataForm.date"
					type="daterange"
					range-separator="~"
					start-placeholder="开始日期"
					end-placeholder="结束日期"
					size="medium"
				></el-date-picker>
			</el-form-item>
			<el-form-item>
				<el-select
					v-model="dataForm.status"
					class="input"
					placeholder="状态"
					size="medium"
					clearable="clearable"
				>
					<el-option label="未缴纳" value="1" />
					<el-option label="已缴纳" value="2" />
				</el-select>
			</el-form-item>
			<el-form-item>
				<el-button size="medium" type="primary" @click="searchHandle()">查询</el-button>
				<el-button
					size="medium"
					type="primary"
					:disabled="!isAuth(['ROOT', 'AMECT:INSERT'])"
					@click="addHandle()"
				>
					新增
				</el-button>
				<el-button
					size="medium"
					type="danger"
					:disabled="!isAuth(['ROOT', 'AMECT:DELETE'])"
					@click="deleteHandle()"
				>
					批量删除
				</el-button>
				<el-button
					size="medium"
					type="warning"
					:disabled="!isAuth(['ROOT', 'AMECT:SELECT'])"
					@click="reportHandle()"
				>
					查看报告
				</el-button>
			</el-form-item>
		</el-form>
		<el-table
			:data="dataList"
			border
			v-loading="dataListLoading"
			@selection-change="selectionChangeHandle"
			cell-style="padding: 4px 0"
			style="width: 100%;"
			size="medium"
		>
			<el-table-column
				type="selection"
				:selectable="selectable"
				header-align="center"
				align="center"
				width="50"
			/>
			<el-table-column width="40px" prop="reason" header-align="center" align="center" type="expand">
				<template #default="scope">
					罚款原因：{{ scope.row.reason }}
				</template>
			</el-table-column>
			<el-table-column type="index" header-align="center" align="center" width="100" label="序号">
				<template #default="scope">
					<span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
				</template>
			</el-table-column>
			<el-table-column prop="type" header-align="center" align="center" label="罚款类型" />
			<el-table-column prop="name" header-align="center" align="center" label="当事人" />
			<el-table-column prop="deptName" header-align="center" align="center" label="所属部门" />
			<el-table-column header-align="center" align="center" label="罚款金额">
				<template #default="scope">
					<span>{{ scope.row.amount }}元</span>
				</template>
			</el-table-column>
			<el-table-column prop="status" header-align="center" align="center" label="状态" />
			<el-table-column prop="createTime" header-align="center" align="center" label="日期时间" />
			<el-table-column fixed="right" header-align="center" align="center" width="150" label="操作">
				<template #default="scope">
					<el-button
						type="text"
						size="medium"
						:disabled="!(isAuth(['ROOT', 'AMECT:UPDATE']) && scope.row.status != '已缴纳')"
						@click="updateHandle(scope.row.id)"
					>
						修改
					</el-button>
					<el-button
						type="text"
						size="medium"
						:disabled="!(isAuth(['ROOT', 'AMECT:DELETE']) && scope.row.status != '已缴纳')"
						@click="deleteHandle(scope.row.id)"
					>
						删除
					</el-button>
					<el-button
						type="text"
						size="medium"
						:disabled="!(scope.row.mine == 'true' && scope.row.status == '未缴纳')"
						@click="payHandle(scope.row.id)"
					>
						交款
					</el-button>
				</template>
			</el-table-column>
		</el-table>
		<el-pagination
			@size-change="sizeChangeHandle"
			@current-change="currentChangeHandle"
			:current-page="pageIndex"
			:page-sizes="[10, 20, 50]"
			:page-size="pageSize"
			:total="totalCount"
			layout="total, sizes, prev, pager, next, jumper"
		></el-pagination>
		<add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="loadDataList"></add-or-update>
		<pay v-if="payVisible" ref="pay" @refreshDataList="loadDataList"></pay>
	</div>
</template>

<script>
import dayjs from 'dayjs';
import AddOrUpdate from './amect-add-or-update.vue';
import Pay from './amect-pay.vue';
export default {
	components: { AddOrUpdate, Pay },
	data: function() {
		return {
			dataForm: {
				name: null,
				deptId: null,
				typeId: null,
				status: null,
				date: null
			},
			deptList: [],
			amectTypeList: [],
			dataList: [],
			pageIndex: 1,
			pageSize: 10,
			totalCount: 0,
			dataListLoading: false,
			dataListSelections: [],
			dataRule: {
				name: [{ required: false, pattern: '^[\u4e00-\u9fa5]{1,10}$', message: '姓名格式错误' }]
			},
			addOrUpdateVisible: false,
			payVisible: false
		};
	},
	methods: {
		loadDeptList: function() {
			let that = this;
			that.$http('dept/searchAllDept', 'GET', null, true, function(resp) {
				that.deptList = resp.list;
			});
		},
		loadAmectTypeList: function() {
			let that = this;
			that.$http('amect_type/searchAllAmectType', 'GET', {}, true, function(resp) {
				that.amectTypeList = resp.list;
			});
		}
	},
	created: function() {
		this.loadDeptList();
		this.loadAmectTypeList();

	}
};
</script>

<style></style>
