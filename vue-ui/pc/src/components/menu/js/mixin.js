import rules from './rules';
import popup from '@assets/js/mixin/popup';
import common from '@assets/js/mixin/common';
import global from '@assets/js/util/global';
import iconfont from '@assets/icon/iconfont.json';
import {mapGetters} from 'vuex';
import {panelTitle, treeSelect} from '@assets/js/common/components';
import {LIST} from '@assets/js/common/action';

const mixin = {
    mixins: [popup, common, rules],
    components: {
        panelTitle, treeSelect,
    },
    data() {
        return {
            form: {
                type: '1',
                level: 1,
            },
            isDisabled: false,
            option: {uuid: 'root', label: '根节点'},
            typeList: [],
            iconfont: iconfont,
            menuDirTreeData: [],
            defaultKeys: [],
        };
    },
    computed: {
        type() {
            return this.form.type;
        },
        level() {
            return this.form.level;
        },
        ...mapGetters(['menuDirTree']),
    },
    created() {
        let self = this;
        self.selectDict('.menus', 'menu-type').then((result) => {
            self.typeList = result;
        });
    },
    methods: {
        /* 返回列表*/
        goBack() {
            this.$emit('nextStep', LIST);
        },
        /* 保存事件*/
        submit() {
            let self = this;
            self.$refs.form.validate((valid) => {
                valid && self.saveMenu();
            });
        },
        /* 选中上级菜单*/
        nodeClick(node) {
            this.checkConfirm(node);
        },
        /* 上级菜单选中确认*/
        checkConfirm(node) {
            this.$refs.treeSelect.setCurrentKey(node.uuid);
            this.option = global.deepClone(node);
            this.form.level = this.option.level + 1;
            this.form.parentId = this.option.uuid;
            this.$refs.parent.blur();
        },
        /* 搜索事件*/
        filterMethod(value) {
            const treeData = global.deepClone(this.menuDirTree);
            if (!value) {
                this.menuDirTreeData = treeData;
            } else {
                this.menuDirTreeData = global.filterTree(treeData, 'label', value);
            }
        },
        /* 清空上级菜单*/
        clear() {
            this.option = global.deepClone({uuid: 'root', label: '根节点'});
            this.form.parentId = 'root';
            this.form.level = 1;
            this.$refs.treeSelect.setCurrentKey(null);
            this.menuDirTreeData = this.menuDirTree;
        },
        /* 设置菜单目录的组件路径*/
        setUrl(level) {
            this.isDisabled = true;
            this.form.url = level === 1 ? '/index/main' : '/index/components/ultimate';
        },
    },
    watch: {
        /* 监听菜单类型*/
        type(newValue) {
            if (newValue === '0') {
                this.setUrl(this.form.level);
            } else {
                this.isDisabled = false;
            }
        },
        /* 监听菜单等级*/
        level(newValue) {
            if (this.form.type === '0') {
                this.setUrl(newValue);
            } else {
                this.isDisabled = false;
            }
        },
    },
};

export default mixin;
