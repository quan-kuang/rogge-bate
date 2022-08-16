<template>
    <div class="wechat">
        <panel-title/>
        <el-button plain type="info" @click="getLocation">获取定位</el-button>
        <el-button plain type="primary" @click="startRecord">开始录音</el-button>
        <el-button plain type="danger" @click="stopRecord">停止录音</el-button>
        <el-button plain type="primary" @click="playVoice">播放录音</el-button>
        <el-button plain type="danger" @click="stopVoice">停止播放</el-button>
        <el-button plain type="warning" @click="uploadVoice">上传录音</el-button>
        <el-button plain type="success" @click="downloadVoice">下载录音</el-button>
        <el-button plain type="success" @click="getAuthUserInfoUrl">网页授权</el-button>
        <div id="qrCode"/>
    </div>
</template>

<script>
import wx from 'weixin-js-sdk';
import '@assets/js/qqmap/geolocation.min';
import popup from '@assets/js/mixin/popup';
import constant from '@assets/js/common/constant';
import cipher from '@assets/js/util/cipher';
import QRCode from 'qrcodejs2';
import {downloadMedia, getAuthUserInfoUrl, getConfig} from '@assets/js/api/wechat';
import {panelTitle} from '@assets/js/common/components';

export default {
    name: 'wechat',
    components: {
        panelTitle,
    },
    mixins: [popup],
    data() {
        return {
            info: this.$route.params.info, // 微信网页授权用户信息
            geolocation: null, // 腾讯地图组件实例
            params: {
                url: location.href.split('#')[0],
                appId: constant.appId,
                appSecret: constant.appSecret,
            },
            jsApiList: [
                'getLocation', // 获取地理位置接口（经纬度）
                'startRecord', // 开始录音接口
                'stopRecord', // 停止录音接口
                'playVoice', // 播放语音接口
                'stopVoice', // 停止播放接口
                'onVoicePlayEnd', // 监听语音播放完毕接口
                'uploadVoice', // 上传语音接口
            ],
            localId: '', // 音频的本地端ID
            serverId: '', // 音频的服务端ID
        };
    },
    created() {
        // 获取微信config配置信息并初始化
        this.getConfig();
        // 获取微信openId
        if (!!this.info) {
            this.info = cipher.decryptAES1(this.info, constant.secretKey);
            this.messageSuccess(this.info);
        }
    },
    methods: {
        /* 使用腾讯地图接口获取定位*/
        geoLocation() {
            // 初始geolocation对象
            this.geolocation = new window.qq.maps.Geolocation(constant.mapKey, constant.referer);
            // 获取当前所在地理位置（经测试PC端和微信端获取地址位置有误差）
            this.geolocation.getLocation(this.sucCallback, this.errCallback);
        },
        /* 微信地图组件定位成功的回调*/
        sucCallback(position) {
            console.log(position);
        },
        /* 微信地图组件定位失败的回调*/
        errCallback() {
            this.messageError('定位失败');
        },
        /* 获取微信config*/
        getConfig() {
            let self = this;
            getConfig(self.params).then((response) => {
                if (response.flag) {
                    self.wxInit(response.data);
                } else {
                    self.messageError(response.msg);
                }
            });
        },
        /* 初始化wx对象*/
        wxInit(data) {
            // 通过config接口注入权限验证配置
            wx.config({
                debug: false, // 开启调试模式
                appId: data.appId, // 必填，公众号的唯一标识
                timestamp: data.timestamp, // 必填，生成签名的时间戳
                nonceStr: data.noncestr, // 必填，生成签名的随机串
                signature: data.signature, // 必填，签名
                jsApiList: this.jsApiList, // 必填，需要使用的JS接口列表
            });
            // 判断当前客户端版本是否支持指定JS接口
            wx.checkJsApi({
                jsApiList: this.jsApiList, // 需要检测的JS接口列表，所有JS接口列表见附录2
                success: function (res) {
                    console.log(res);
                },
                fail: function (res) {
                    console.log(res);
                },
            });
            // 通过ready接口处理成功验证
            wx.ready(function () {
                // config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
            });
            // 通过error接口处理失败验证
            wx.error(function (res) {
                // config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
            });
        },
        /* 微信JSSDK获取定位接口*/
        getLocation() {
            let self = this;
            wx.getLocation({
                type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
                success: function (res) {
                    self.messageSuccess(`经度：${res.longitude}、纬度：${res.latitude}`);
                },
            });
        },
        /* 微信JSSDK开始录音接口*/
        startRecord() {
            wx.startRecord();
        },
        /* 微信JSSDK停止录音接口*/
        stopRecord() {
            let self = this;
            wx.stopRecord({
                success: function (res) {
                    self.localId = res.localId;
                },
            });
        },
        /* 微信JSSDK播放录音接口*/
        playVoice() {
            let self = this;
            wx.playVoice({
                localId: self.localId, // 需要播放的音频的本地ID，由stopRecord接口获得
            });
        },
        /* 微信JSSDK停止播放录音接口*/
        stopVoice() {
            let self = this;
            wx.stopVoice({
                localId: self.localId, // 需要停止的音频的本地ID，由stopRecord接口获得
            });
        },
        /* 微信JSSDK上传录音接口*/
        uploadVoice() {
            let self = this;
            wx.uploadVoice({
                localId: self.localId, // 需要上传的音频的本地ID，由stopRecord接口获得
                isShowProgressTips: 1, // 默认为1，显示进度提示
                success: function (res) {
                    self.serverId = res.serverId; // 返回音频的服务端ID
                    self.messageSuccess('上传成功');
                },
            });
        },
        /* 自定义后台下载录音接口，并将微信提供的arm格式录音转换成mp3*/
        downloadVoice() {
            let self = this;
            if (!self.serverId || self.serverId === '') {
                self.messageError('请先上传录音');
                return;
            }
            const loading = self.loadingOpen('.wechat');
            downloadMedia(self.serverId).then((response) => {
                if (response.flag) {
                    const downloadUrl = constant.viewLocalUrl + response.data;
                    window.open(downloadUrl, '_blank');
                } else {
                    self.messageError(response.msg);
                }
            }).finally(() => {
                loading && loading.close();
            });
        },
        /* 微信网页授权获取用户信息*/
        getAuthUserInfoUrl() {
            let self = this;
            const data = {
                scope: 'snsapi_base', // 应用授权作用域
                redirectUrl: encodeURI(constant.apiUrl + 'wechat/getAuthUserInfo'), // 授权后重定向的回调接口
                state: encodeURI(self.params.url.substring(0, self.params.url.lastIndexOf('/') + 1)), // 重定向后会带上state参数，此处传入路由地址用于回跳
            };
            getAuthUserInfoUrl(data).then((response) => {
                if (response.flag) {
                    const getAuthUserInfoUrl = response.data;
                    self.scanQRCodeLogin(getAuthUserInfoUrl);
                    // window.open(getAuthUserInfoUrl, '_blank');
                } else {
                    self.messageError(response.msg);
                }
            });
        },
        scanQRCodeLogin(text) {
            let self = this;
            self.isShowQRCode = !self.isShowQRCode;
            if (self.isShowQRCode) {
                self.$nextTick(() => {
                    self.qrcode(text);
                });
            } else {
                document.getElementById('qrCode').innerHTML = '';
            }
        },
        qrcode(text) {
            let qrcode = new QRCode('qrCode', {
                text: text, // 二维码内容
                width: 295,
                height: 295, // 高度  [图片上传失败...(image-9ad77b-1525851843730)]
                colorDark: '#333333', // 二维码颜色
                colorLight: '#ffffff', // 二维码背景色
                correctLevel: QRCode.CorrectLevel.L, // 容错率，L/M/H
            });
            console.log(qrcode);
        },
    },
};
</script>
