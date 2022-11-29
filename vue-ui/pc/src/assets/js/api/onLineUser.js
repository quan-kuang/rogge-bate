import request from '@/assets/js/config/request';

/* 查询在线用户*/
export function selectOnLineUser(data) {
    return request({
        url: 'system/onLineUser/selectOnLineUser',
        method: 'post',
        data: data,
    });
}

/* 删除在线用户*/
export function deleteOnLineUser(data) {
    return request({
        url: 'system/onLineUser/deleteOnLineUser',
        method: 'post',
        data: data,
    });
}
