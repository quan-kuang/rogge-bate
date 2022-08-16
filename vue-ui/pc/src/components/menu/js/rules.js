import verify from '@assets/js/util/verify';

const rules = {
    data() {
        /* 路由地址校验*/
        const checkPath = (rule, value, callback) => {
            // 截取最前面的/符号
            if (value.substr(0, 1) === '/') {
                this.form.path = value.substring(1);
            }
            // 截取最后面的/符号
            if (value.charAt(value.length - 1) === '/') {
                this.form.path = value.substring(0, value.length - 1);
            }
            // 格式化成功取消校验
            this.$refs['form'].clearValidate(['type']);
            return callback();
        };
        /* 空格校验*/
        const checkBlank = (rule, value, callback) => {
            if (verify.regularCheck(value, verify.regExpBlank)) {
                return callback(new Error('不能包含空格'));
            }
            return callback();
        };
        /* 输入有空格校验*/
        const checkHaveBlank = (rule, value, callback) => {
            if (!!value && verify.regularCheck(value, verify.regExpBlank)) {
                return callback(new Error('不能包含空格'));
            }
            return callback();
        };
        /* 组件路径存在校验*/
        const checkUrl = (rule, value, callback) => {
            require(['@/components' + value]).then(() => {
                return callback();
            }).catch(() => {
                return callback(new Error('路径不存在'));
            });
        };
        return {
            rules: {
                parentId: [
                    {required: true, message: '请选择上级目录', trigger: 'blur'},
                ],
                title: [
                    {required: true, message: '请输入菜单名称', trigger: 'blur'},
                    {validator: checkBlank, trigger: 'change'},
                ],
                path: [
                    {required: true, message: '请输入路由地址', trigger: 'blur'},
                    {validator: checkBlank, trigger: 'change'},
                    {validator: checkPath, trigger: 'blur'},
                ],
                url: [
                    {required: true, message: '请输入组件路径', trigger: 'blur'},
                    {validator: checkBlank, trigger: 'change'},
                    {validator: checkUrl, trigger: 'change'},
                ],
                name: [
                    {validator: checkHaveBlank, trigger: 'blur'},
                ],
                redirect: [
                    {validator: checkHaveBlank, trigger: 'blur'},
                ],
                permission: [
                    {required: true, message: '请输入权限标识', trigger: 'blur'},
                    {validator: checkHaveBlank, trigger: 'blur'},
                ],
            },
        };
    },
};

export default rules;
