import rules from './rules';
import popup from '@assets/js/mixin/popup';
import {panelTitle, treeSelect} from '@assets/js/common/components';
import {LIST} from '@assets/js/common/action';

const mixin = {
    mixins: [popup, rules],
    components: {
        panelTitle, treeSelect,
    },
    data() {
        return {};
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
                valid && self.saveConstant();
            });
        },
    },
};

export default mixin;
