import request from '@/assets/js/config/request';

/* 附件上传*/
export function upload(data) {
    return request({
        url: 'system/attachment/upload',
        method: 'post',
        data: data,
    });
}

/* 保存附件信息*/
export function saveAttachment(data) {
    return request({
        url: 'system/attachment/saveAttachment',
        method: 'post',
        data: data,
    });
}

/* 删除附件信息*/
export function deleteAttachment(data) {
    return request({
        url: 'system/attachment/deleteAttachment',
        method: 'post',
        data: data,
    });
}

/* 查询附件信息*/
export function selectAttachment(data) {
    return request({
        url: 'system/attachment/selectAttachment',
        method: 'post',
        data: data,
    });
}
