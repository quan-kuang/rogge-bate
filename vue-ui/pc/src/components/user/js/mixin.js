import rules from './rules';
import popup from '@assets/js/mixin/popup';
import global from '@assets/js/util/global';
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
        /* 选中上级部门*/
        nodeClick(node) {
            // 此处过滤只能选中子节点
            if (node.hasOwnProperty('children')) {
                // return;
            }
            this.$refs.treeSelect.setCurrentKey(node.uuid);
            this.option = global.deepClone(node);
            this.form.deptId = this.option.uuid;
            this.$refs.dept.blur();
        },
        /* 搜索事件*/
        filterMethod(value) {
            const treeData = global.deepClone(this.params.cloneDeptList);
            this.params.deptList = !value ? treeData : global.filterTree(treeData, 'name', value);
        },
        /* 清空上级部门*/
        clear() {
            this.option = {uuid: '', name: ''};
            this.form.deptId = '';
            this.$refs.treeSelect.setCurrentKey(null);
            this.params.deptList = global.deepClone(this.params.cloneDeptList);
        },
    },
};

export default mixin;
