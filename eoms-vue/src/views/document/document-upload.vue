<template>
    <el-dialog title="上传文件" width="520px" :close-on-click-modal="false" v-model="visible" :show-close="false">
        <div align="center">
            <el-upload
                    ref="upload"
                    :action="url"
                    accept=".txt,.jpg,.jpeg,.png,.pdf,.doc,.docx,.xls,.xlsx,.ppt"
                    drag
                    multiple
                    with-credentials="true"
                    :before-upload="beforeUploadHandle"
                    :on-success="successHandle"
                    :on-remove="removeHandle"
            >
                <div>
                    <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg" data-v-ba633cb8="" class="icon-svg">
                        <path fill="currentColor" d="M160 832h704a32 32 0 1 1 0 64H160a32 32 0 1 1 0-64zm384-578.304V704h-64V247.296L237.248 490.048 192 444.8 508.8 128l316.8 316.8-45.312 45.248L544 253.696z"></path></svg>
                </div>
                <div style="color: #409EFF">拖拽或点击上传</div>
            </el-upload>
        <div>
            <el-form :model="dataForm" ref="dataForm" :rules="dataRule">
                <el-row>
                    <el-col :span="11">
                        <el-form-item label="类型" prop="isPublic" label-width="60px">
                            <el-select v-model="dataForm.isPublic" size="small" style="width: 100%;" clearable>
                                <el-option label="公开" value="1"></el-option>
                                <el-option label="私有" value="0"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col>
                        <el-form-item>
                            <el-button size="small" @click="cancel()">取消</el-button>
                            <el-button type="primary" @click="uploadFile()" size="small" :disabled="disableBtn">{{ btn }}</el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        </div>
    </el-dialog>
</template>

<script>
    export default {
        data: function () {
            return {
                visible: false,
                url: this.$baseUrl + 'cos/uploadCosFile?type=document',
                disableBtn: false,
                picList: {},
                btn: '确定',
                dataForm: {
                    isPublic: null
                },
                originName: null,
                dataRule: {
                    isPublic: [{ required: true, message: '是否公开不能为空' }],
                }
            };
        },
        methods: {
            init: function () {
                this.visible = true
                this.btn = '上传文件'
                this.disableBtn = false
                this.$nextTick(() => {
                    this.$refs['upload'].clearFiles()
                    this.picList = []
                })
            },
            beforeUploadHandle: function (file) {
                // if (file.type !== 'application/msword' & file.type !== 'text/plain' & file.type !== 'image/jpg' && file.type !== 'application/pdf' && file.type !== 'image/jpeg' && file.type !== 'image/png' ) {
                //     this.$message.error('只支持.doc、txt、jpg、png、pdf格式的文件')
                //     return false
                // }
                // return true
                const fileSuffix = file.name.substring(file.name.lastIndexOf(".") + 1);
                const whiteList = ["pdf", "doc", "docx", "xls", "xlsx", "txt", "jpg", "jpeg", "ppt", "png"];
                if (whiteList.indexOf(fileSuffix) === -1) {
                    this.$message.error('只支持pdf,doc,docx,xls,xlsx,txt,jpg,jpeg格式的文件')
                    return false;
                }
            },
            successHandle: function (resp, file, fileList) {
                console.log(fileList)
                if (resp && resp.code === 200) {
                    // 将图片的url和相对路径存储
                    for (let one of fileList) {
                        this.originName = one.name
                        this.picList[one.response.url] = one.response.path
                    }
                } else {
                    this.$message.error('文件上传失败');
                }
            },
            removeHandle: function (file) {
                let that = this
                let url = file.response.url
                let path = that.picList[url]
                that.$http('/cos/deleteCosFile', "POST", {paths: [path]}, true, function (resp) {
                    delete that.picList[url]
                })
            },
            cancel: function () {
                let that = this
                if (Object.keys(that.picList).length > 0) {
                    let paths = Object.values(that.picList)
                    that.$http('/cos/deleteCosFile', "POST", {paths: paths}, true, function (resp) {
                        that.picList = {}
                    })
                }
                that.visible = false
                that.$refs['upload'].clearFiles()
            },
            uploadFile: function () {
                let that = this
                // 防止重复提交
                that.btn = '正在提交'
                that.disableBtn = true
                if (Object.keys(that.picList).length === 0) {
                    that.$message({
                        message: '没有上传文件',
                        type: 'error',
                        duration: 1200
                    });
                    that.btn = '上传文件'
                    that.disableBtn = false
                    return;
                }
                that.$refs['dataForm'].validate((valid) => {
                    if (valid) {
                        let filePath = that.picList[Object.keys(that.picList)[0]]
                        let fileUrl = Object.keys(that.picList)[0]
                        let fileName = that.originName
                        let data = {
                            documentName: fileName,
                            path: filePath,
                            url: fileUrl,
                            isPublic: that.dataForm.isPublic
                        }
                        console.log(data)
                        that.$http('/document/addDocument', 'POST', data, true, function (resp) {
                            if (resp.rows === 1) {
                                that.$message({
                                    message: '上传成功',
                                    type: 'success',
                                    duration: 1200
                                });
                                that.visible = false;
                                that.$emit('refreshDataList')
                            } else {
                                that.$message({
                                    message: '上传失败',
                                    type: 'error',
                                    duration: 1200
                                });
                            }
                        })
                    } else {
                        that.btn = '上传文件'
                        that.disableBtn = false
                        return false
                    }
                })
            }
        }
    }
</script>

<style lang="less" scoped>
    .icon-svg {
        width: 9rem;
        height: 9rem;
        fill: currentColor;
        overflow: hidden;
        margin-right: 8px;
    }
</style>