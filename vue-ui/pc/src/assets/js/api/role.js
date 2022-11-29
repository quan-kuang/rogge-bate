import request from '@/assets/js/config/request';

/* 保存角色*/
export function saveRole(data) {
    return request({
        url: 'system/role/saveRole',
        method: 'post',
        data: data,
    });
}

/* 查询角色*/
export function selectRole(data) {
    return request({
        url: 'system/role/selectRole',
        method: 'post',
        data: data,
    });
}

/* 删除角色*/
export function deleteRole(data) {
    return request({
        url: 'system/role/deleteRole',
        method: 'post',
        data: data,
    });
}

/* 修改角色信息*/
export function updateRole(data) {
    return request({
        url: 'system/role/updateRole',
        method: 'post',
        data: data,
    });
}

/* 查询角色关联菜单/部门ID*/
export function selectRoleLink(data) {
    return request({
        url: 'system/role/selectRoleLink',
        method: 'post',
        data: data,
    });
}

/* 校验角色是否存在*/
export function checkRoleExists(value) {
    return request({
        url: 'system/role/checkRoleExists/' + value,
        method: 'get',
    });
}
