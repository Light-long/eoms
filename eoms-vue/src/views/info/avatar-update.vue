<template>
	<el-dialog title="更换头像(只上传一张)" width="400px" :close-on-click-modal="false" v-model="visible" :show-close="false">
		<el-upload
			class="avatar-uploader"
			:class="{disabled: uploadDisabled}"
			ref="upload"
			:action="url"
			list-type="picture-card"
			:file-list="imageList"
			accept=".jpg,.jpeg,.png,.pdf"
			with-credentials="true"
			:before-upload="beforeUploadHandle"
			:on-success="successHandle"
			:on-remove="removeHandle"
		>
			<i class="el-icon-plus"></i>
		</el-upload>
		<template #footer>
			<span class="dialog-footer">
				<el-button size="small" @click="cancel()">取消</el-button>
				<el-button type="primary" @click="archive()" size="small" :disabled="disableBtn">{{ btn }}</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
export default {
	data: function() {
		return {
			visible: false,
			url: this.$baseUrl + 'cos/uploadCosFile?type=archive',
			taskId: '',
			picList: {},
			disableBtn: false,
			btn: '确定',
			imageList: []
		};
	},
	computed: {

		uploadDisabled:function() {
			return this.imageList.length >0
		},
	},
	methods: {
		init: function () {
			this.visible = true
			this.btn = '上传头像'
			this.disableBtn = false
			this.$nextTick(() => {
				this.$refs['upload'].clearFiles()
			})
		},
		beforeUploadHandle: function (file) {
			if (file.type !== 'image/jpg' && file.type !== 'application/pdf' && file.type !== 'image/jpeg' && file.type !== 'image/png' ) {
				this.$message.error('只支持jpg、png格式的图片！')
				return false
			}
			return true
		},
		successHandle: function (resp, file, fileList) {
			console.log(fileList)
			if (resp && resp.code === 200) {
				// 将图片的url和相对路径存储
				for (let one of fileList) {
					 this.picList[one.response.url] = one.response.path
				}
			} else {
				this.$message.error('图片上传失败');
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
		archive: function () {
			let that = this
			// 防止重复提交
			that.btn = '正在归档'
			that.disableBtn = true
			if (Object.keys(that.picList).length === 0) {
				that.$message({
					message: '没有上传归档照片',
					type: 'error',
					duration: 1200
				});
				return;
			}
			let files = [];
			for (let key in that.picList) {
				files.push({
					url: key,
					path: that.picList[key]
				});
			}
			let data = {
				taskId: that.taskId,
				files: JSON.stringify(files)
			};
			that.$http('/approval/archiveTask', 'POST', data, true, function (resp) {
				that.$message({
					message: '操作成功',
					type: 'success',
					duration: 1200
				});
				that.visible = false;
				that.$emit('refreshDataList')
			})
		}
	}
};
</script>

<style lang="less">
	.avatar-uploader .el-upload {
		border: 1px dashed #d9d9d9;
		border-radius: 6px;
		cursor: pointer;
		position: relative;
		overflow: hidden;
		transition: var(--el-transition-duration-fast);
	}
	.avatar-uploader .el-upload:hover {
		border-color: var(--el-color-primary);
	}
	.el-icon.avatar-uploader-icon {
		font-size: 28px;
		color: #8c939d;
		width: 178px;
		height: 178px;
		text-align: center;
	}

	.disabled .el-upload--picture-card {
		display: none;
	}

	.avatar {
		width: 178px;
		height: 178px;
		display: block;
	}
</style>
