<template>
	<el-dialog title="更换头像(只用上传一张)" width="500px" :close-on-click-modal="false" v-model="visible" :show-close="false">
		<el-upload
				ref="upload"
				:action="url"
				list-type="picture-card"
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
				<el-button size="medium" @click="cancel()">取消</el-button>
				<el-button type="primary" @click="updateAvatar()" size="medium" :disabled="disableBtn">{{ btn }}</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
	export default {
		data: function() {
			return {
				visible: false,
				url: this.$baseUrl + 'cos/uploadCosFile?type=avatar',
				picList: {},
				disableBtn: false,
				btn: '上传图片'
			};
		},
		methods: {
			init: function () {
				this.visible = true
				this.btn = '上传图片'
				this.disableBtn = false
				this.$nextTick(() => {
					this.$refs['upload'].clearFiles()
					this.picList = []
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
			updateAvatar: function () {
				let that = this
				// 防止重复提交
				that.btn = '正在提交'
				that.disableBtn = true
				if (Object.keys(that.picList).length === 0) {
					that.$message({
						message: '没有上传头像照片',
						type: 'error',
						duration: 1200
					});
					that.btn = '上传图片'
					that.disableBtn = false
					return;
				}
				let avatar = Object.keys(that.picList)[0]
				let data = {
					avatar: avatar
				}
				that.$http('/user/updateAvatar', 'POST', data, true, function (resp) {
					if (resp.rows === 1) {
						that.$message({
							message: '更换头像成功',
							type: 'success',
							duration: 1200
						});
						that.visible = false;
						that.$emit('refreshDataList')
					} else {
						that.$message({
							message: '更换头像失败',
							type: 'error',
							duration: 1200
						});
					}
				})
			}
		}
	};
</script>

<style lang="less"></style>
