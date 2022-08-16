import verify from '@assets/js/util/verify';
import {checkDictExists} from '@assets/js/api/dict';

const rules = {
    data() {
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
        /* 字典父节点存在校验*/
        const checkDictParentId = async (rule, value, callback) => {
            if (!this.form.value) {
                return callback();
            }
            if (!!this.cloneForm && this.cloneForm.parentId === value) {
                return callback();
            }
            const data = {value: this.form.value, parentId: value};
            await this.checkDictExists(data).then((result) => {
                if (result === null) {
                    return callback(new Error('校验失败'));
                }
                if (result > 0) {
                    return callback(new Error(`${value}已存在`));
                }
            });
            this.$refs['form'].clearValidate(['value']);
            return callback();
        };
        /* 字典键值存在校验*/
        const checkDictValue = async (rule, value, callback) => {
            if (!this.form.parentId) {
                return callback();
            }
            if (!!this.cloneForm && this.cloneForm.value === value) {
                return callback();
            }
            if (!!value && verify.regularCheck(value, verify.regExpChinese)) {
                return callback(new Error('不能包含中文'));
            }
            const data = {value: value, parentId: this.form.parentId};
            await this.checkDictExists(data).then((result) => {
                if (result === null) {
                    return callback(new Error('校验失败'));
                }
                if (result > 0) {
                    return callback(new Error(`${value}已存在`));
                }
            });
            this.$refs['form'].clearValidate(['parentId']);
            return callback();
        };
        /* 字典主键存在校验*/
        const checkDictUuid = async (rule, value, callback) => {
            let count = 0;
            if (!value) {
                return callback();
            }
            if (!!this.cloneForm && this.cloneForm.uuid === value) {
                return callback();
            }
            if (!!value && verify.regularCheck(value, verify.regExpChinese)) {
                return callback(new Error('不能包含中文'));
            }
            await this.checkDictExists(value).then((result) => {
                count = result;
            });
            if (count > 0) {
                return callback(new Error(`${value}已存在`));
            }
            return callback();
        };
        return {
            rules: {
                parentId: [
                    {required: true, message: '请选择上级字典', trigger: 'blur'},
                    {validator: checkDictParentId, trigger: 'change'},
                ],
                value: [
                    {required: true, message: '请输入字典键值', trigger: 'blur'},
                    {validator: checkBlank, trigger: 'change'},
                    {validator: checkDictValue, trigger: 'blur'},
                ],
                text: [
                    {required: true, message: '请输入字典名称', trigger: 'blur'},
                    {validator: checkBlank, trigger: 'change'},
                ],
                uuid: [
                    {validator: checkHaveBlank, trigger: 'change'},
                    {validator: checkDictUuid, trigger: 'blur'},
                ],
            },
        };
    },
    methods: {
        /* 校验字典项是否存在*/
        checkDictExists(data) {
            return new Promise((resolve) => {
                checkDictExists(data).then((response) => {
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
