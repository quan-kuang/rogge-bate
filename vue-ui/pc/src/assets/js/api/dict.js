import request from '@/assets/js/config/request';

/* 保存字典信息*/
export function saveDict(data) {
    return request({
        url: 'system/dict/saveDict',
        method: 'post',
        data: data,
    });
}

/* 删除字典信息*/
export function deleteDict(data) {
    return request({
        url: 'system/dict/deleteDict',
        method: 'post',
        data: data,
    });
}

/* 查询字典信息*/
export function selectDict(data) {
    return request({
        url: 'system/dict/selectDict',
        method: 'post',
        data: data,
    });
}

/* 级联查询字典信息*/
export function selectCascade(data) {
    return request({
        url: 'system/dict/selectCascade',
        method: 'post',
        data: data,
    });
}

/* 校验字典项是否存在*/
export function checkDictExists(data) {
    return request({
        url: 'system/dict/checkDictExists',
        method: 'post',
        data: data,
    });
}
