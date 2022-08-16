import request from '@/assets/js/config/request';

/* 发送邮件*/
export function sendMail(data) {
    return request({
        url: 'message/news/sendMail',
        method: 'post',
        data: data,
    });
}

/* 发送短信验证码*/
export function sendCaptcha(data) {
    return request({
        url: 'message/news/sendCaptcha',
        method: 'post',
        data: data,
    });
}

/* 查询redis同步任务日志*/
export function selectRedisShakeLog(data) {
    return request({
        url: 'message/news/selectRedisShakeLog?openWebsocket',
        method: 'post',
        // data: data,
    });
}
