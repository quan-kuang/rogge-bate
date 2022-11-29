import request from '@/assets/js/config/request';

/* 百度人脸搜索*/
export function faceDetect(params) {
    return request({
        url: 'message/baidu/faceDetect',
        method: 'post',
        params: params,
    });
}

/* 百度人脸检测*/
export function faceVerify(params) {
    return request({
        url: 'message/baidu/faceVerify',
        method: 'post',
        params: params,
    });
}

/* 百度身份证识别*/
export function ocrIdCard(data) {
    return request({
        url: 'message/baidu/ocrIdCard',
        method: 'post',
        data: data,
    });
}
