import verify from '@assets/js/util/verify';

const rules = {
    data() {
        /* 空格校验*/
        const checkBlank = (rule, value, callback) => {
            if (value.length > 0 && verify.regularCheck(value, verify.regExpBlank)) {
                return callback(new Error('不能包含空格'));
            }
            return callback();
        };
        return {
            rules: {
                user: {
                    account: [
                        {required: true, message: '请输入必填项', trigger: 'blur'},
                        {validator: checkBlank, trigger: 'blur'},
                    ],
                    password: [
                        {required: true, message: '请输入必填项', trigger: 'blur'},
                        {validator: checkBlank, trigger: 'blur'},
                    ],
                },
                captcha: {
                    value: [
                        {required: true, message: '请输入验证码', trigger: 'blur'},
                        {validator: checkBlank, trigger: 'blur'},
                    ],
                },
            },
        };
    },
};

export default rules;
