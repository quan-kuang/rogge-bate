import verify from '@assets/js/util/verify';
import {checkRoleExists} from '@assets/js/api/role';

const rules = {
    data() {
        /* 空格校验*/
        const checkBlank = (rule, value, callback) => {
            if (verify.regularCheck(value, verify.regExpBlank)) {
                return callback(new Error('不能包含空格'));
            }
            return callback();
        };
        /* 角色名称校验*/
        const checkAccount = async (rule, value, callback) => {
            let count = null;
            if (this.form.uuid === null) {
                await this.checkRoleExists(value).then((result) => {
                    count = result;
                });
                if (count === null) {
                    return callback(new Error('角色存在校验失败'));
                } else if (count > 0) {
                    return callback(new Error(`${value}已存在`));
                }
            }
            return callback();
        };
        /* 菜单校验*/
        const checkMenuIds = (rule, value, callback) => {
            if (value && value.length === 0) {
                return callback(new Error('请选择菜单'));
            }
            return callback();
        };
        /* 部门校验*/
        const checkDeptIds = (rule, value, callback) => {
            value = this.form.deptIds;
            if (this.form.permissionType === '1' && value && value.length === 0) {
                return callback(new Error('请选择部门'));
            }
            return callback();
        };
        return {
            rules: {
                name: [
                    {required: true, message: '请输入角色名称', trigger: 'blur'},
                    {validator: checkBlank, trigger: 'blur'},
                    {validator: checkAccount, trigger: 'blur'},
                ],
                permissionType: [
                    {required: true, message: '请选择权限部门', trigger: ['blur', 'change']},
                ],
                menuIds: [
                    {validator: checkMenuIds, trigger: 'change'},
                ],
                deptIds: [
                    {validator: checkDeptIds, trigger: 'change'},
                ],
            },
        };
    },
    methods: {
        /* 校验角色是否存在*/
        checkRoleExists(name) {
            return new Promise((resolve) => {
                checkRoleExists(name).then((response) => {
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
