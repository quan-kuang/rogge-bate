import request from '@/assets/js/config/request';

/* 保存定时任务*/
export function saveCrontab(data) {
    return request({
        url: 'system/crontab/saveCrontab',
        method: 'post',
        data: data,
    });
}

/* 删除定时任务*/
export function deleteCrontab(data) {
    return request({
        url: 'system/crontab/deleteCrontab',
        method: 'post',
        data: data,
    });
}

/* 查询定时任务*/
export function selectCrontab(data) {
    return request({
        url: 'system/crontab/selectCrontab',
        method: 'post',
        data: data,
    });
}

/* 校验corn表达式是否有效*/
export function checkCornExpression(params) {
    return request({
        url: 'system/crontab/checkCornExpression',
        method: 'get',
        params: params,
    });
}

/* 执行定时任务*/
export function executeCrontab(data) {
    return request({
        url: 'system/crontab/executeCrontab',
        method: 'post',
        data: data,
    });
}

/* 删除定时任务日志*/
export function deleteCrontabLog(data) {
    return request({
        url: 'system/crontab/deleteCrontabLog',
        method: 'post',
        data: data,
    });
}

/* 查询定时任务日志*/
export function selectCrontabLog(data) {
    return request({
        url: 'system/crontab/selectCrontabLog',
        method: 'post',
        data: data,
    });
}

/* 获取表达式执行时间*/
export function getExecuteTime(params) {
    return request({
        url: 'system/crontab/getExecuteTime',
        method: 'get',
        params: params,
    });
}
