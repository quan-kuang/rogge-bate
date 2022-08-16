import request from '@/assets/js/config/request';

/* 登录接口*/
export function login(data) {
    return request({
        url: 'system/user/login',
        method: 'post',
        data: data,
    });
}
