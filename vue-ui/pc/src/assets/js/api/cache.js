import request from '@/assets/js/config/request';

/* 获取当前Redis信息*/
export function getRedis(type, params) {
    return request({
        url: 'tools/cache/getRedis/' + type,
        method: 'get',
        params: params,
    });
}

/* 设置当前Redis配置*/
export function setRedis(data) {
    return request({
        url: 'tools/cache/setRedis',
        method: 'post',
        data: data,
    });
}

/* 获取当前Jedis信息*/
export function getJedis(type, data, params) {
    return request({
        url: 'tools/cache/getJedis/' + type,
        method: 'post',
        data: data,
        params: params,
    });
}

/* 设置当前Jedis配置*/
export function setJedis(data) {
    return request({
        url: 'tools/cache/setJedis',
        method: 'post',
        data: data,
    });
}

/* 查询缓存列表*/
export function selectCacheInfo(data) {
    return request({
        url: 'tools/cache/selectCacheInfo',
        method: 'post',
        data: data,
    });
}

/* 查询缓存详情*/
export function selectCacheInfoDetails(params) {
    return request({
        url: 'tools/cache/selectCacheInfoDetails',
        method: 'get',
        params: params,
    });
}

/* 删除缓存信息*/
export function deleteCacheInfo(data) {
    return request({
        url: 'tools/cache/deleteCacheInfo',
        method: 'post',
        data: data,
    });
}

/* 保存缓存信息*/
export function saveCacheInfo(data) {
    return request({
        url: 'tools/cache/saveCacheInfo',
        method: 'post',
        data: data,
    });
}
