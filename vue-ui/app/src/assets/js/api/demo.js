import request from '@/assets/js/config/request';

/* 查询版本号*/
export function showVersion() {
    return request({
        url: 'system/demo/showVersion',
        method: 'get',
    });
}
