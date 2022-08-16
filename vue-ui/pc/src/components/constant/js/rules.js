import verify from '@assets/js/util/verify';
import {selectConstant} from '@assets/js/api/constant';

const rules = {
    data() {
        /* 空格校验*/
        const checkBlank = (rule, value, callback) => {
            if (verify.regularCheck(value, verify.regExpBlank)) {
                return callback(new Error('不能包含空格'));
            }
            return callback();
        };
        /* 校验键是否存在*/
        const checkConstantExists = async (rule, value, callback) => {
            if (this.form.id) {
                return callback();
            }
            await this.checkConstantExists(value).then((result) => {
                if (result === null) {
                    return callback(new Error('校验失败'));
                }
                if (result.length > 0) {
                    return callback(new Error(`${value}已存在`));
                }
            });
            return callback();
        };
        return {
            rules: {
                name: [
                    {required: true, message: '请输入名称', trigger: 'blur'},
                    {validator: checkBlank, trigger: 'change'},
                ],
                key: [
                    {required: true, message: '请输入键名', trigger: 'blur'},
                    {validator: checkBlank, trigger: 'change'},
                    {validator: checkConstantExists, trigger: 'change'},
                ],
                value: [
                    {required: true, message: '请输入键值', trigger: 'blur'},
                    {validator: checkBlank, trigger: 'change'},
                ],
            },
        };
    },
    methods: {
        /* 校验系统常量是否存在*/
        checkConstantExists(value) {
            return new Promise((resolve) => {
                const data = {key: value};
                selectConstant(data).then((response) => {
                    if (response.flag) {
                        resolve(response.data);
                    } else {
                        resolve(null);
                    }
                });
            });
        },
    },
};

export default rules;
