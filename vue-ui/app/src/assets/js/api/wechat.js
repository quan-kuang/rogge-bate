import request from '@/assets/js/config/request';

/* 获取微信JSSDK接口配置*/
export function getConfig(data) {
    return request({
        url: 'system/wechat/getConfig',
        method: 'post',
        data: data,
    });
}

/* 下载微信服务器上的媒体文件*/
export function downloadMedia(value) {
    return request({
        url: 'system/wechat/downloadMedia/' + value,
        method: 'get',
    });
}

/* 获取微信网页授权登录的跳转链接*/
export function getAuthUserInfoUrl(params) {
    return request({
        url: 'system/wechat/getAuthUserInfoUrl',
        params: params,
    });
}

