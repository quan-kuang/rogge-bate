import request from '@/assets/js/config/request';

/* 获取验证码*/
export function getCaptcha(data) {
    return request({
        url: 'system/util/getCaptcha',
        method: 'post',
        data: data,
    });
}
