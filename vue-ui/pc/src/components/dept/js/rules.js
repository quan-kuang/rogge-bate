import verify from '@assets/js/util/verify';
import {selectDept} from '@assets/js/api/dept';

const rules = {
    data() {
        /* 空格校验*/
        const checkBlank = (rule, value, callback) => {
            if (verify.regularCheck(value, verify.regExpBlank)) {
                return callback(new Error('不能包含空格'));
            }
            return callback();
        };
        /* 部门键值存在校验*/
        const checkDeptName = async (rule, value, callback) => {
            let count = null;
            if (this.form.uuid === null) {
                await this.checkDeptExists(value).then((result) => {
                    count = result;
                });
                if (count === null) {
                    return callback(new Error('部门存在校验失败'));
                } else if (count > 0) {
                    return callback(new Error(`${value}已存在`));
                }
            }
        };
        return {
            rules: {
                parentId: [
                    {required: true, message: '请选择上级部门', trigger: ['blur', 'change']},
                ],
                name: [
                    {required: true, message: '请输入部门名称', trigger: 'blur'},
                    {validator: checkBlank, trigger: 'change'},
                    {validator: checkDeptName, trigger: 'blur'},
                ],
            },
        };
    },
    methods: {
        /* 校验部门项是否存在*/
        checkDeptExists(value) {
            return new Promise((resolve) => {
                const data = {name: value};
                selectDept(data).then((response) => {
                    if (response.flag) {
                        resolve(response.data.length);
                    } else {
                        resolve(null);
                    }
                });
            });
        },
    },
};

export default rules;
