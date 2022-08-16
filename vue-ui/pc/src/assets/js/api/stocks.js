import request from '@/assets/js/config/request';

/* 获取股票信息*/
export function getList(data) {
    return request({
        url: 'message/stocks/getList',
        method: 'post',
        data: data,
    });
}
