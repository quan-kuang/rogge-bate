import request from '@/assets/js/config/request';

/* 查询胎动记录*/
export function selectEcgInfo(params) {
    return request({
        url: 'system/qianYu/selectEcgInfo',
        method: 'get',
        params: params,
    });
}
