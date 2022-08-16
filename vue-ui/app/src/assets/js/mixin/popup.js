const popup = {
    data() {
        return {};
    },
    methods: {
        /* 弹窗提示*/
        toast(message, type) {
            if (type === 'T') {
                this.$toast.success(message);
            } else if (type === 'F') {
                this.$toast.fail(message);
            } else {
                this.$toast(message);
            }
        },
        /* 弹窗提示*/
        alert(message, title) {
            this.$dialog.alert({
                title: title,
                message: message,
            }).then(() => {
                // on close
            });
        },
        /* 服务加载中*/
        loadingOpen(message) {
            return this.$toast.loading({
                duration: 0, // 持续展示
                forbidClick: true,
                message: message,
            });
        },
    },
};

export default popup;
