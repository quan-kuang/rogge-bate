import rules from './rules';
import popup from '@assets/js/mixin/popup';
import {panelTitle, tree} from '@assets/js/common/components';
import {LIST} from '@assets/js/common/action';
import {mapGetters} from 'vuex';

const mixin = {
    mixins: [popup, rules],
    components: {
        panelTitle, tree,
    },
    computed: {
        ...mapGetters(['menuTree']),
    },
    data() {
        return {
            props: {
                value: 'uuid',
                label: 'name',
                children: 'children',
            },
            defaultMenuKeys: [],
            defaultDeptKeys: [],
        };
    },
    methods: {
        /* 返回列表*/
        goBack() {
            this.$emit('nextStep', LIST);
        },
        /* 保存事件*/
        saveEvent() {
            let self = this;
            self.form.menuIds = self.$refs.menuTree.getCheckedKeys();
            // 自定义数据权限赋值
            if (self.form.permissionType === '1') {
                self.form.deptIds = self.$refs.deptTree.getCheckedKeys();
            }
            self.$refs.form.validate((valid) => {
                valid && self.saveRole();
            });
        },
    },
};

export default mixin;
