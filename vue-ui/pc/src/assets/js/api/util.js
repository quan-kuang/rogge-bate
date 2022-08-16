import request from '@/assets/js/config/request';

/* 获取验证码*/
export function getCaptcha(data) {
    return request({
        url: 'system/util/getCaptcha',
        method: 'post',
        data: data,
    });
}

/* 上传Vue项目*/
export function putVue(value) {
    return request({
        url: 'system/util/getSreverInfo/' + value,
        method: 'get',
    });
}

/* 发布Vue项目*/
export function releaseVue(value) {
    return request({
        url: 'system/util/releaseVue/' + value,
        method: 'get',
    });
}

/* 查询数据库模式*/
export function selectSchemaName() {
    return request({
        url: 'system/util/selectSchemaName',
        method: 'get',
    });
}

/* 查询表说明信息*/
export function selectTableExplain(data) {
    return request({
        url: 'system/util/selectTableExplain',
        method: 'post',
        data: data,
    });
}

/* 获取当前服务器信息*/
export function getSreverInfo() {
    return request({
        url: 'tools/util/getSreverInfo',
        method: 'get',
    });
}

/* 创建PDF*/
export function createPdf(data) {
    return request({
        url: 'tools/util/createPdf',
        method: 'post',
        data: data,
    });
}

/* 获取指定包下所有类名*/
export function getClassName(params) {
    return request({
        url: 'tools/util/getClassName',
        method: 'get',
        params: params,
    });
}

/* 获取指定类下所有方法*/
export function getMethod(params) {
    return request({
        url: 'tools/util/getMethod',
        method: 'get',
        params: params,
    });
}

/* 反射动态调用方法*/
export function invoke(data) {
    return request({
        url: 'tools/util/invoke',
        method: 'post',
        data: data,
    });
}
