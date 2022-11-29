import request from '@/assets/js/config/request';

/* 登录接口*/
export function login(data) {
    return request({
        url: 'system/user/login',
        method: 'post',
        data: data,
    });
}

/* 短信登录接口*/
export function messageLogin(data) {
    return request({
        url: 'system/user/messageLogin',
        method: 'post',
        data: data,
    });
}

/* 保存用户信息*/
export function saveUser(data) {
    return request({
        url: 'system/user/saveUser',
        method: 'post',
        data: data,
    });
}

/* 查询用户信息*/
export function selectUser(data) {
    return request({
        url: 'system/user/selectUser',
        method: 'post',
        data: data,
    });
}

/* 删除用户信息*/
export function deleteUser(data) {
    return request({
        url: 'system/user/deleteUser',
        method: 'post',
        data: data,
    });
}

/* 修改用户信息*/
export function updateUser(data) {
    return request({
        url: 'system/user/updateUser',
        method: 'post',
        data: data,
    });
}

/* 根据ID查询单条用户信息*/
export function selectUserById(value) {
    return request({
        url: 'system/user/selectUserById/' + value,
        method: 'get',
    });
}

/* 精确查询单条用户信息*/
export function selectUserBy(data) {
    return request({
        url: 'system/user/selectUserBy',
        method: 'post',
        data: data,
    });
}

/* 校验用户是否存在*/
export function checkUserExists(value) {
    return request({
        url: 'system/user/checkUserExists/' + value,
        method: 'get',
    });
}

/* 实人认证*/
export function auth(data) {
    return request({
        url: 'system/user/auth',
        method: 'post',
        data: data,
    });
}
