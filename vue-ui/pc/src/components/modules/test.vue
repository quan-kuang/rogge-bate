<template>
    <div>
        <panel-title/>
        <el-button plain type="primary" @click="getValue('931851631')">getValue</el-button>
        <el-button plain type="success" @click="setValue">setValue</el-button>
        <el-button plain type="info" @click="queryDataBase">queryDataBase</el-button>
        <el-button plain type="warning" @click="createPdf">createPdf</el-button>
        <el-button plain type="danger" @click="setCookie">setCookie</el-button>
        <el-button plain type="primary" @click="getCookie">getCookie</el-button>
        <el-button plain type="success" @click="sendMail">sendMail</el-button>
        <el-button plain type="primary" @click="consolePrint">consolePrint</el-button>
        <el-button plain type="success" @click="selectRedisShakeLog">selectRedisShakeLog</el-button>
    </div>
</template>

<script>
import {panelTitle} from '@assets/js/common/components';
import popup from '@assets/js/mixin/popup';
import constant from '@assets/js/common/constant';
import {consolePrint, getCookie, getValue, queryDataBase, setValue} from '@assets/js/api/demo';
import {createPdf} from '@assets/js/api/util';
import {selectRedisShakeLog, sendMail} from '@assets/js/api/news';

export default {
    name: 'test',
    components: {
        panelTitle,
    },
    mixins: [popup],
    data() {
        return {
            params: {
                url: location.href.split('#')[0],
                appId: constant.appId,
                appSecret: constant.appSecret,
            },
        };
    },
    methods: {
        getValue(key) {
            let self = this;
            getValue(key).then((response) => {
                self.message(response.flag ? 'success' : 'error', response.msg);
            });
        },
        setValue() {
            let self = this;
            const data = {
                'key': '931851631',
                'value': self.params,
            };
            setValue(data).then((response) => {
                self.message(response.flag ? 'success' : 'error', response.msg);
            });
        },
        queryDataBase() {
            let self = this;
            queryDataBase().then((response) => {
                self.message(response.flag ? 'success' : 'error', response.msg);
            });
        },
        setCookie() {
            let self = this;
            self.$cookies.set('name', 'kuangq');
            self.$cookies.set('age', '28');
            alert(self.$cookies.get('name'));
        },
        getCookie() {
            let self = this;
            getCookie().then((response) => {
                self.message(response.flag ? 'success' : 'error', response.msg);
            });
        },
        createPdf() {
            let self = this;
            const data = {};
            createPdf(data).then((response) => {
                self.message(response.flag ? 'success' : 'error', response.msg);
                if (response.flag) {
                    const downloadUrl = constant.viewLocalUrl + response.data;
                    window.open(downloadUrl, '_blank');
                }
            });
        },
        sendMail() {
            let self = this;
            self.$prompt('请输入收件邮箱', '', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                inputPattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
                inputErrorMessage: '邮箱地址格式不正确',
            }).then(({value}) => {
                let body = "<div style='width:580px;margin:0 auto;padding:10px;background-color:aliceblue;'>";
                body += "<div style='height:23px;background:url(https://loyer.wang/view/local/image/mail_head_bg.png) repeat-x 0 0;'></div>";
                body += "<div style='text-align:left;padding:30px;font-size:14px;line-height:1.5;'>";
                body += "<div><p style='font-weight: 800;font-size: 18px; color: crimson;'>通知</p><p style='color: cornflowerblue;'>这是一封测试邮件</p></div></div></div>";
                const data = {
                    sender: 'rogbet',
                    receiver: value,
                    subject: '测试',
                    body: body,
                    fileResourceList: [
                        {
                            name: 'rogbet.jpg',
                            type: 'URL',
                            content: 'https://loyer.wang/view/local/image/me.jpg',
                            place: '1',
                        },
                    ],
                };
                sendMail(data).then((response) => {
                    self.message(response.flag ? 'success' : 'error', response.msg);
                });
            });
        },
        consolePrint() {
            let self = this;
            consolePrint().then((response) => {
                !response.flag && self.messageError(response.msg);
            });
        },
        selectRedisShakeLog() {
            let self = this;
            selectRedisShakeLog().then((response) => {
                !response.flag && self.messageError(response.msg);
            });
        },
    },
};
</script>
