<template>
    <!-- 添加或修改公告对话框 -->
    <el-dialog :title="!dataForm.id ? '添加公告' : '修改公告'"
               :close-on-click-modal="false"
               v-model="visible"
               width="680px"
    >
        <el-form ref="dataForm" :model="dataForm" :rules="dataRule" label-width="120px">
            <el-row>
                <el-col :span="11">
                    <el-form-item label="公告标题" prop="title" class="beBold">
                        <el-input v-model="dataForm.title" placeholder="请输入公告标题" />
                    </el-form-item>
                </el-col>
                <el-col :span="11">
                    <el-form-item label="公告状态" prop="status" class="beBold">
                        <el-select v-model="dataForm.status" placeholder="状态">
                            <el-option label="正常" value="1"></el-option>
                            <el-option label="关闭" value="0"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="是否置顶" prop="isTopping" class="beBold">
                        <el-radio-group v-model="dataForm.isTopping" size="large">
                            <el-radio :label="0">不置顶</el-radio>
                            <el-radio :label="1">置顶</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="内容" prop="content" class="beBold">
                        <el-input
                                type="textarea"
                                v-model="dataForm.content"
                                placeholder="公告详细内容"
                                :autosize="{ minRows: 5, maxRows: 6 }"
                                clearable="clearable"
                        />
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <template #footer>
			<span class="dialog-footer">
				<el-button size="medium" @click="visible = false">取消</el-button>
				<el-button type="primary" size="medium" @click="dataFormSubmit">确定</el-button>
			</span>
        </template>
    </el-dialog>
</template>

<script>
    export default {
        name: "notice-add-update",
        data: function() {
            return {
                visible: false,
                dataForm: {
                    title: null,
                    status: null,
                    isTopping: null,
                    content: null
                },
                dataRule: {
                    title: [{ required: true, message: '公告标题为必填' }],
                    status: [{ required: true, message: '公告状态必填' }],
                    isTopping: [{ required: true, message: '是否置顶必填' }],
                    content: [{ required: true, message: '公告内容必填'}]
                }
            };
        },
        methods: {
            init: function (id) {
                let that = this;
                that.dataForm.id = id || 0;
                that.visible = true;
                that.$nextTick(() => {
                    that.$refs['dataForm'].resetFields();
                    // 根据id回显公告信息
                    if (id) {
                        that.$http('/notice/searchNoticeById', 'POST', { id: id }, true, function(resp) {
                            let notice = resp.notice
                            that.dataForm.title = notice.title
                            that.dataForm.content = notice.content
                            that.dataForm.status = notice.status + ""
                            that.dataForm.isTopping = notice.isTopping
                        });
                    }
                })
            },
            dataFormSubmit: function () {
                let that = this
                let data = {
                    title: that.dataForm.title,
                    content: that.dataForm.content,
                    status: that.dataForm.status,
                    isTopping: that.dataForm.isTopping
                }
                // 更新需要传入id
                if (that.dataForm.id) {
                    data.id = that.dataForm.id;
                }
                this.$refs['dataForm'].validate(valid => {
                    if (valid) {
                        that.$http(`/notice/${!that.dataForm.id ? 'addNotice' : 'updateNotice'}`, 'POST', data, true, function (resp) {
                            if (resp.rows === 1) {
                                that.visible = false;
                                that.$message({
                                    message: '操作成功',
                                    type: 'success',
                                    duration: 1200
                                });
                                that.$emit('refreshDataList');
                            } else {
                                that.$message({
                                    message: '操作公告失败',
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
    }
</script>

<style lang="less">
    .beBold .el-form-item__label {
        font-size: 15px;
        font-weight: bold;
    }
</style>