import request from '@/assets/js/config/request';

/* 保存部门信息*/
export function saveDept(data) {
    return request({
        url: 'system/dept/saveDept',
        method: 'post',
        data: data,
    });
}

/* 删除部门信息*/
export function deleteDept(data) {
    return request({
        url: 'system/dept/deleteDept',
        method: 'post',
        data: data,
    });
}

/* 查询部门信息*/
export function selectDept(data) {
    return request({
        url: 'system/dept/selectDept',
        method: 'post',
        data: data,
    });
}

/* 级联查询部门信息*/
export function selectCascade(data) {
    return request({
        url: 'system/dept/selectCascade',
        method: 'post',
        data: data,
    });
}
