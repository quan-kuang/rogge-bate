import Clipboard from 'clipboard';
import popup from '@assets/js/mixin/popup';

const copy = {
    mixins: [popup],
    data() {
        return {};
    },
    methods: {
        /* 点击复制*/
        hbdtwx(event, msg, hint) {
            let self = this;
            if (!msg) {
                return;
            }
            hint = hint || '';
            const clipboard = new Clipboard(event.target, {text: () => msg});
            clipboard.on('success', (e) => {
                // 释放内存
                clipboard.off('error');
                clipboard.off('success');
                clipboard.destroy();
                self.toast(hint + '复制成功');
            });
            clipboard.on('error', (e) => {
                // 释放内存
                clipboard.off('error');
                clipboard.off('success');
                clipboard.destroy();
                self.toast(hint + '复制失败');
            });
        },
    },
};

export default copy;
