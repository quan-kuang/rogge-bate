import rules from './rules';
import global from '@assets/js/util/global';
import format from '@assets/js/util/format';
import popup from '@assets/js/mixin/popup';
import {selectDept} from '@assets/js/api/dept';
import {panelTitle, treeSelect} from '@assets/js/common/components';
import {LIST} from '@assets/js/common/action';

const mixin = {
    mixins: [popup, rules],
    components: {
        panelTitle, treeSelect,
    },
    data() {
        return {
            props: {
                value: 'uuid',
                label: 'name',
                children: 'children',
            },
            defaultKeys: [],
            deptList: [],
            cloneDeptList: [],
            option: {uuid: '', name: ''},
        };
    },
    methods: {
        /* 返回列表*/
        goBack() {
            this.$emit('nextStep', LIST);
        },
        /* 查询部门列表*/
        initDept(data, selector) {
            let self = this;
            return new Promise((resolve) => {
                const loading = self.loadingOpen(selector);
                selectDept(data).then((response) => {
                    if (response.flag) {
                        resolve(format.list(response.data, data.parentId));
                    } else {
                        self.messageError(response.msg);
                        resolve([]);
                    }
                }).finally(() => {
                    loading && loading.close();
                });
            });
        },
        /* 选中上级部门*/
        nodeClick(node) {
            this.$refs.treeSelect.setCurrentKey(node.uuid);
            this.option = global.deepClone(node);
            this.form.parentId = this.option.uuid;
            this.$refs.dept.blur();
        },
        /* 搜索事件*/
        filterMethod(value) {
            const treeData = global.deepClone(this.params.cloneDeptList);
            if (!value) {
                this.params.deptList = treeData;
            } else {
                this.params.deptList = global.filterTree(treeData, 'name', value);
            }
        },
        /* 清空上级部门*/
        clear() {
            this.option = {uuid: '', name: ''};
            this.form.parentId = '';
            this.$refs.treeSelect.setCurrentKey(null);
            this.params.deptList = global.deepClone(this.params.cloneDeptList);
        },
        /* 保存事件*/
        submit() {
            let self = this;
            self.$refs.form.validate((valid) => {
                valid && self.saveDept();
            });
        },
    },
};

export default mixin;
