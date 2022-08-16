import request from '@/assets/js/config/request';

/* 查询操作日志*/
export function selectOperateLog(data) {
    return request({
        url: 'system/log/selectOperateLog',
        method: 'post',
        data: data,
    });
}

/* ES查询操作日志*/
export function selectOperateLogEs(data) {
    return request({
        url: 'system/log/selectOperateLogEs',
        method: 'post',
        data: data,
    });
}

/* 查询操作日志详细出入参*/
export function selectOperateArgs(params) {
    return request({
        url: 'system/log/selectOperateArgs',
        method: 'post',
        params: params,
    });
}

/* 多线程查询操作日志*/
export function selectOperateLogThread(data) {
    return request({
        url: 'system/log/selectOperateLogThread',
        method: 'post',
        data: data,
    });
}

/* 日志周统计*/
export function logWeekStat(data) {
    return request({
        url: 'system/log/logWeekStat',
        method: 'post',
        data: data,
    });
}

/* 删除日志信息*/
export function deleteOperateLog(data) {
    return request({
        url: 'system/log/deleteOperateLog',
        method: 'post',
        data: data,
    });
}
