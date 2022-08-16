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
            // 限制上传文件数
            fileLimit: 100,
            // 限制上传文件大小M
            fileSize: 10,
            // 结果集总条数
            fileTotal: 0,
            // 结果集
            fileList: [],
            // 待上传附件
            uploadFiles: [],
        };
    },
    methods: {
        /* 上传文件之前的钩子*/
        beforeUpload(file) {
            let size = file.size / 1024 / 1024;
            if (this.fileSize < size) {
                this.msgbox('附件大小超出范围');
                return false;
            }
            // 设置上传位置
            const sources = this.fileData.sources;
            if (sources.length > 0) {
                this.fileData.source = String(sources[Math.floor(Math.random() * sources.length)]);
            } else {
                this.fileData.source = '';
            }
            // 刷新请求头信息
            this.headers = cipher.getHeaders();
        },
        /* 文件上传成功时的钩子*/
        onSuccess(response, file) {
            if (response.flag) {
                this.messageSuccess(`${file.name}上传成功`);
            } else {
                this.messageError(response.msg);
            }
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
        /* 删除文件之前的钩子*/
        beforeRemove(file) {
            return this.$confirm(`确定移除 ${file.name}？`);
        },
        /* 文件列表移除文件时的钩子*/
        onRemove(file) {
            for (let index = 0; index < this.uploadFiles.length; index++) {
                if (file.uid === this.uploadFiles[index].uid) {
                    this.uploadFiles.splice(index, 1);
                    this.messageSuccess(`附件${file.name}删除成功`);
                    return true;
                }
            }
        },
        /* 获取附件URL*/
        getFileUrl(row) {
            switch (row.source) {
                case '0':
                    return constant.viewLocalUrl + row.path;
                case '1':
                    return constant.viewFtpUrl + row.path;
                case '2':
                    return constant.viewFastDfsUrl + row.path;
                default:
                    return '/';
            }
        },
    },
};

export default file;
