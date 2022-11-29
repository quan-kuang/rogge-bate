import request from '@/assets/js/config/request';

/* 查询版本号*/
export function showVersion() {
    return request({
        url: 'system/demo/showVersion',
        method: 'get',
    });
}

/* 查询数据库基本信息*/
export function queryDataBase() {
    return request({
        url: 'system/demo/queryDataBase',
        method: 'get',
    });
}

/* 保存数据到redis*/
export function setValue(data) {
    return request({
        url: 'tools/demo/setValue',
        method: 'post',
        data: data,
    });
}

/* 获取缓存数据*/
export function getValue(value) {
    return request({
        url: 'tools/demo/getValue/' + value,
        method: 'get',
    });
}

/* 获取Cookie*/
export function getCookie() {
    return request({
        url: 'system/demo/getCookie',
        method: 'get',
    });
}

/* 控制台打印*/
export function consolePrint() {
    return request({
        url: 'message/demo/consolePrint?openWebsocket',
        method: 'get',
    });
}
