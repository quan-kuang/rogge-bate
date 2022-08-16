import request from '@/assets/js/config/request';

/* 保存菜单信息*/
export function saveMenu(data) {
    return request({
        url: 'system/menu/saveMenu',
        method: 'post',
        data: data,
    });
}

/* 查询菜单信息*/
export function selectMenu(data) {
    return request({
        url: 'system/menu/selectMenu',
        method: 'post',
        data: data,
    });
}

/* 删除菜单信息*/
export function deleteMenu(data) {
    return request({
        url: 'system/menu/deleteMenu',
        method: 'post',
        data: data,
    });
}

/* 修改菜单信息*/
export function updateMenu(data) {
    return request({
        url: 'system/menu/updateMenu',
        method: 'post',
        data: data,
    });
}

/* 菜单级联查询*/
export function selectCascade(data) {
    return request({
        url: 'system/menu/selectCascade',
        method: 'post',
        data: data,
    });
}
