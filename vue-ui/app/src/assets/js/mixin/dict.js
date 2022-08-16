import popup from '@assets/js/mixin/popup';

const copy = {
    mixins: [popup],
    methods: {
        /* 查询字典信息*/
        selectDict(parentId) {
            let self = this;
            return new Promise((resolve, reject) => {
                const url = 'dict/selectDict';
                const data = {
                    status: true,
                    parentId: parentId,
                };
                const loading = self.loadingOpen('加载中...');
                self.request.post(url, data).then((response) => {
                    if (response.data.flag) {
                        resolve(response.data.data);
                    } else {
                        self.alert(response.data.msg);
                    }
                }).finally(() => {
                    loading.clear();
                });
            });
        },
    },
};

export default copy;
