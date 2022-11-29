import request from '@/assets/js/config/request';

/* 保存常量信息*/
export function saveConstant(data) {
    return request({
        url: 'system/constant/saveConstant',
        method: 'post',
        data: data,
    });
}

/* 删除常量信息*/
export function deleteConstant(data) {
    return request({
        url: 'system/constant/deleteConstant',
        method: 'post',
        data: data,
    });
}

/* 查询常量信息*/
export function selectConstant(data) {
    return request({
        url: 'system/constant/selectConstant',
        method: 'post',
        data: data,
    });
}
