<template>
    <div>
        <van-button type="info" @click="getLocation">获取定位</van-button>
        <van-button type="primary" @click="startRecord">开始录音</van-button>
        <van-button type="warning" @click="stopRecord">停止录音</van-button>
        <van-button type="danger" @click="playVoice">播放录音</van-button>

        <van-button type="info" @click="stopVoice">停止播放</van-button>
        <van-button type="primary" @click="uploadVoice">上传录音</van-button>
        <van-button type="warning" @click="downloadVoice">下载录音</van-button>
        <van-button type="danger" @click="chooseImage">图像接口</van-button>

        <van-button type="info" @click="previewImage">预览图片</van-button>
        <van-button type="primary" @click="uploadImage">上传图片</van-button>
        <van-button type="warning" @click="downloadImage">下载图片</van-button>
        <van-button type="danger" @click="getLocalImgData">获取本地</van-button>

        <van-button type="info" @click="test">拍照测试</van-button>
    </div>
</template>

<script>
import wx from 'weixin-js-sdk';
import '@assets/js/qqmap/geolocation.min';
import popup from '@assets/js/mixin/popup';
import constant from '@assets/js/common/constant';
import cipher from '@assets/js/util/cipher';
import {downloadMedia, getConfig} from '@assets/js/api/wechat';

export default {
    name: 'wechat',
    mixins: [popup],
    data() {
        return {
            openId: this.$route.params.openId,
            geolocation: null, // 腾讯地图组件实例
            params: {
                url: location.href.split('#')[0],
                appID: constant.appID,
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
                'chooseImage', // 图像接口
                'previewImage', // 预览图片接口
                'uploadImage', // 上传图片接口
                'downloadImage', // 下载图片接口
                'getLocalImgData', // 获取本地图片接口
            ],
            localId: '', // 音频的本地端ID
            serverId: '', // 音频的服务端ID
        };
    },
    created() {
        // 获取微信config配置信息并初始化
        this.getConfig();
        // 获取微信openId
        if (!!this.openId) {
            this.openId = cipher.decryptAES1(this.openId, constant.secretKey);
            this.toast(this.openId, 'T');
        }
    },
    methods: {
        /* 获取微信config*/
        getConfig() {
            let self = this;
            getConfig(self.params).then((response) => {
                if (response.data.flag) {
                    self.wxInit(response.data.data);
                } else {
                    self.toast(response.data.msg, 'F');
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
            this.toast('定位失败', 'F');
        },
        /* 微信JSSDK获取定位接口*/
        getLocation() {
            let self = this;
            wx.getLocation({
                type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
                success: function (res) {
                    self.toast(`经度：${res.longitude}、纬度：${res.latitude}`, 'T');
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
                    self.toast('上传成功', 'T');
                },
            });
        },
        /* 自定义后台下载录音接口，并将微信提供的arm格式录音转换成mp3*/
        downloadVoice() {
            let self = this;
            if (!self.serverId || self.serverId === '') {
                self.toast('请先上传录音', 'F');
                return;
            }
            const loading = self.loadingOpen('加载中...');
            downloadMedia(self.serverId).then((response) => {
                if (response.data.flag) {
                    const downloadUrl = constant.viewLocalUrl + response.data.data;
                    window.open(downloadUrl, '_blank');
                } else {
                    self.toast(response.data.msg, 'F');
                }
            }).finally(() => {
                loading.clear();
            });
        },
        chooseImage() {
            let self = this;
            wx.chooseImage({
                count: 1, // 默认9
                sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
                sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
                success: function (res) {
                    let localIds = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
                    self.toast(localIds);
                },
            });
        },
        previewImage() {
            // TODO
        },
        uploadImage() {
            // TODO
        },
        downloadImage() {
            // TODO
        },
        getLocalImgData() {
            // TODO
        },
        test() {
            let input = '';
            input = document.createElement('input');
            input.setAttribute('id', 'input_upload_ID');
            input.setAttribute('type', 'file');
            input.setAttribute('capture', 'camera');
            input.setAttribute('accept', 'image/*');
            input.setAttribute('style', 'visibility:hidden');
            input.addEventListener('change', this.uploadImg, false);
            document.body.appendChild(input);
            input.click();
        },
        uploadImg(event) {
            let oFile = event.target.files[0];
            console.log(oFile);// 打印值看下面图片,简单点的话我们直接把这个数据给后台处理就可以了


        },
    },
};
</script>

