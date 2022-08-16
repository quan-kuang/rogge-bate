import verify from '@assets/js/util/verify';
import {checkCornExpression, selectCrontab} from '@assets/js/api/crontab';

const rules = {
    data() {
        /* 空格校验*/
        const checkBlank = (rule, value, callback) => {
            if (verify.regularCheck(value, verify.regExpBlank)) {
                return callback(new Error('不能包含空格'));
            }
            return callback();
        };
        /* 任务名称校验*/
        const checkCrontabName = async (rule, value, callback) => {
            let count = null;
            if (this.form.uuid === null) {
                await this.checkCrontabName(value).then((result) => {
                    count = result;
                });
                if (count === null) {
                    return callback(new Error('任务存在校验失败'));
                } else if (count > 0) {
                    return callback(new Error(`${value}已存在`));
                }
            }
        };
        /* 校验corn表达式是否有效*/
        const checkCornExpression = async (rule, value, callback) => {
            await this.checkCornExpression(value).then((result) => {
                if (!result.flag) {
                    return callback(new Error(result.msg));
                }
            });
            return callback();
        };
        return {
            rules: {
                name: [
                    {required: true, message: '请输入任务名称', trigger: 'blur'},
                    {validator: checkBlank, trigger: 'change'},
                    {validator: checkCrontabName, trigger: 'blur'},
                ],
                type: [
                    {required: true, message: '请选择任务类型', trigger: 'change'},
                ],
                invokeTarget: [
                    {required: true, message: '请输入调用目标', trigger: 'blur'},
                    {validator: checkBlank, trigger: 'change'},
                ],
                expression: [
                    {required: true, message: '请输入调用目标', trigger: 'blur'},
                    {validator: checkCornExpression, trigger: 'blur'},
                ],
            },
        };
    },
    methods: {
        /* 校验任务是否存在*/
        checkCrontabName(value) {
            return new Promise((resolve) => {
                const data = {name: value};
                selectCrontab(data).then((response) => {
                    if (response.flag) {
                        resolve(response.data.length);
                    } else {
                        resolve(null);
                    }
                });
            });
        },
        /* 校验corn表达式是否有效*/
        checkCornExpression(value) {
            return new Promise((resolve) => {
                const data = {cornExpression: value};
                checkCornExpression(data).then((response) => {
                    resolve(response);
                });
            });
        },
    },
};

export default rules;
