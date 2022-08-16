const popup = {
    methods: {
        /* 成功弹框*/
        messageSuccess(message) {
            this.message('success', message);
        },
        /* 警告弹框*/
        messageWaning(message) {
            this.message('warning', message);
        },
        /* 失败弹框*/
        messageError(message) {
            this.message('error', message);
        },
        /* 信息弹框*/
        messageInfo(message) {
            this.message('info', message);
        },
        /* 提示弹框*/
        notify(type, message) {
            this.$notify({
                title: '提示',
                type: type,
                message: message,
                duration: 3000,
            });
        },
        /* 提示弹框*/
        message(type, message) {
            this.$message({
                showClose: true,
                type: type,
                message: message,
                duration: 1500,
            });
        },
        /* 提示弹框*/
        msgbox(message) {
            const cons = this.$createElement;
            this.$msgbox({
                title: '提示',
                message: cons('p', {style: 'color: red;text-align: center;'}, message),
            });
        },
        /* 提示弹框*/
        alertBox(html, callbackEvent) {
            this.$alert(html, '提示', {
                dangerouslyUseHTMLString: true,
                confirmButtonText: '确定',
                callback: callbackEvent,
            });
        },
        /* 输入弹框*/
        prompt() {
            // TODO
        },
        /* 确认弹框*/
        confirm(message, confirmEvent, cancelEvent) {
            this.$confirm(message, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }).then(() => {
                confirmEvent.call();
            }).catch(() => {
                if (cancelEvent !== null) {
                    cancelEvent.call();
                }
            });
        },
        /* 服务加载中*/
        loadingOpen(selector, text) {
            const options = {
                lock: true,
                text: !text ? 'loading...' : text,
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.03)',
            };
            // 设置指定动画区域
            const selectorObj = document.querySelector(selector);
            if (!!selectorObj) {
                // 已经存在 避免重复加载
                if (selectorObj.getElementsByClassName('el-loading-mask').length > 0) {
                    return null;
                }
                options.target = selectorObj;
            }
            return this.$loading(options);
        },
    },
};

export default popup;
