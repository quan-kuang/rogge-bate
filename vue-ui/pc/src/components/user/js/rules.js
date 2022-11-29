import verify from '@assets/js/util/verify';
import {checkUserExists} from '@assets/js/api/user';

const rules = {
    data() {
        /* 账号校验*/
        const checkAccount = async (rule, value, callback) => {
            let exist = null;
            await this.checkUserExists(value).then((result) => {
                exist = result;
            });
            if (exist) {
                return callback(new Error(`${value}已存在`));
            }
            return callback();
        };
        /* 空格校验*/
        const checkBlank = (rule, value, callback) => {
            if (verify.regularCheck(value, verify.regExpBlank)) {
                return callback(new Error('不能包含空格'));
            }
            return callback();
        };
        /* 手机号校验*/
        const checkPhone = (rule, value, callback) => {
            if (!verify.regularCheck(value, verify.regExpPhone)) {
                return callback(new Error('手机号格式错误'));
            }
            return callback();
        };
        /* 身份证号校验*/
        const checkIdCard = (rule, value, callback) => {
            if (value) {
                if (verify.regularCheck(value, verify.regExpIdCard)) {
                    this.form.sex = String(parseInt(value.charAt(value.length - 1)) % 2);
                    this.form.birthday = this.insertStr(this.insertStr(value.substr(6, 8), 4, '-'), 7, '-');
                } else {
                    this.form.sex = '';
                    this.form.birthday = '';
                    return callback(new Error('身份证号格式错误'));
                }
            }
            return callback();
        };
        /* 校验确认密码*/
        const checkConfirmPassword = (rule, value, callback) => {
            if (value !== this.form.password) {
                callback(new Error('两次输入密码不一致'));
            }
            return callback();
        };
        return {
            rules: {
                account: [
                    {required: true, message: '请输入账号', trigger: 'blur'},
                    {validator: checkBlank, trigger: 'blur'},
                    {validator: checkAccount, trigger: 'blur'},
                ],
                name: [
                    {required: true, message: '请输入姓名', trigger: 'blur'},
                    {validator: checkBlank, trigger: 'blur'},
                ],
                phone: [
                    {required: true, message: '请输入手机号', trigger: 'blur'},
                    {validator: checkPhone, trigger: 'blur'},
                ],
                idCard: [
                    {validator: checkIdCard, trigger: 'blur'},
                ],
                email: [
                    {type: 'email', message: '邮箱格式错误', trigger: ['blur', 'change']},
                ],
                deptId: [
                    {required: true, message: '请选择部门', trigger: ['blur', 'change']},
                ],
                oldPassword: [
                    {required: true, message: '请输入旧密码', trigger: 'blur'},
                ],
                password: [
                    {required: true, message: '请输入密码', trigger: 'blur'},
                    {validator: checkBlank, trigger: 'blur'},
                ],
                confirmPassword: [
                    {required: true, message: '请再次输入密码', trigger: 'blur'},
                    {validator: checkConfirmPassword, trigger: 'blur'},
                ],
                roleIds: [
                    {required: true, message: '请选择角色', trigger: 'change'},
                ],
            },
        };
    },
    methods: {
        /* 字符串插入字符*/
        insertStr(source, index, value) {
            return source.slice(0, index) + value + source.slice(index);
        },
        /* 校验账户是否存在*/
        checkUserExists(account) {
            return new Promise((resolve) => {
                checkUserExists(account).then((response) => {
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
