import rules from './rules';
import popup from '@assets/js/mixin/popup';
import global from '@assets/js/util/global';
import format from '@assets/js/util/format';
import {selectDict} from '@assets/js/api/dict';
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
                label: 'text',
                children: 'children',
            },
            defaultKeys: [],
            dictList: [],
            option: {uuid: 'root', text: '根节点'},
        };
    },
    methods: {
        /* 返回列表*/
        goBack() {
            this.$emit('nextStep', LIST);
        },
        /* 查询字典列表*/
        initDict(data, selector) {
            let self = this;
            return new Promise((resolve) => {
                const loading = self.loadingOpen(selector);
                selectDict(data).then((response) => {
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
        /* 选中上级字典*/
        nodeClick(node) {
            this.$refs.treeSelect.setCurrentKey(node.uuid);
            this.option = global.deepClone(node);
            this.form.parentId = this.option.uuid;
            this.$refs.parent.blur();
        },
        /* 清空上级字典*/
        clear() {
            this.option = global.deepClone({uuid: 'root', text: '根节点'});
            this.form.parentId = 'root';
            this.$refs.treeSelect.setCurrentKey(null);
        },
        /* 保存事件*/
        submit() {
            let self = this;
            self.$refs.form.validate((valid) => {
                valid && self.saveDict();
            });
        },
    },
};

export default mixin;
