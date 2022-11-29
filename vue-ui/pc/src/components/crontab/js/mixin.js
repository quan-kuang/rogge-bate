import rules from './rules';
import popup from '@assets/js/mixin/popup';
import {panelTitle} from '@assets/js/common/components';
import hint from '@components/crontab/components/hint';
import {LIST} from '@assets/js/common/action';

const mixin = {
    mixins: [popup, rules],
    components: {
        panelTitle, hint,
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
                if (valid) {
                    const isType = typeof self.form.concurrent;
                    if (isType !== 'boolean') {
                        self.form.concurrent = self.form.concurrent.toBool();
                    }
                    self.saveCrontab();
                }
            });
        },
    },
};

export default mixin;
