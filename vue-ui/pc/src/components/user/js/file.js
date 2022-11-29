import popup from '@assets/js/mixin/popup';
import cipher from '@assets/js/util/cipher';
import constant from '@assets/js/common/constant';

const file = {
    mixins: [popup],
    data() {
        return {
            // 附件上传地址
            uploadUrl: constant.apiUrl + 'system/attachment/upload',
            // 请求头
            headers: cipher.getHeaders(),
            // 上传附件类型
            fileType: 'jpeg/jpg/png/bmp',
            // 限制上传文件数
            fileLimit: 1,
            // 限制上传文件大小M
            fileSize: 10,
            // 上传文件
            uploadFiles: [],
            // 隐藏放大图
            isImage: false,
            // 隐藏放大图路径
            imageSrc: '',
        };
    },
    methods: {
        /* 上传文件之前的钩子*/
        beforeUploadPhoto(file) {
            const flag = this.beforeUpload(file);
            if (!flag) {
                return flag;
            }
            const uploadBox = this.$refs.upload.$children[1].$el.style;
            uploadBox.display = 'none';
        },
        /* 上传文件之前的钩子*/
        beforeUpload(file) {
            let type = file.name.substring(file.name.lastIndexOf('.') + 1).toLocaleLowerCase();
            if (this.fileType.indexOf(type) < 0) {
                this.msgbox(`请选择格式为${this.fileType}的附件`);
                return false;
            }
            let size = file.size / 1024 / 1024;
            if (this.fileSize < size) {
                this.msgbox('附件大小超出范围');
                return false;
            }
            return true;
        },
        /* 删除文件之前的钩子*/
        beforeRemove() {
            const uploadBox = this.$refs.upload.$children[1].$el.style;
            uploadBox.opacity = 0;
            uploadBox.display = 'block';
            uploadBox.transition = '1s';
            setTimeout(() => {
                uploadBox.opacity = 1;
            }, 500);
        },
        /* 文件超出个数限制时的钩子*/
        onExceed(files, fileList) {
            this.messageWaning(`当前限制选择${this.fileLimit}个文件，本次选择了${files.length}个文件，共选择了${files.length + fileList.length}个文件`);
        },
        /* 点击已上传的文件链接时的钩子*/
        onPreview(file) {
            this.isImage = true;
            this.imageSrc = file.url;
        },
        /* 文件上传成功时的钩子*/
        onSuccess(response, file, fileList) {
            if (response.flag) {
                response.data.uid = file.uid;
                this.uploadFiles.push(response.data);
            } else {
                this.messageError(response.msg);
                fileList.forEach((item, index, array) => {
                    if (file.uid === item.uid) {
                        array.splice(index, 1);
                        return false;
                    }
                });
            }
        },
    },
};

export default file;
