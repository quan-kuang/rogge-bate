import axios from 'axios';
import cipher from '@assets/js/util/cipher';
import constant from '@assets/js/common/constant';
import global from '@assets/js/util/global';
import {Message} from 'element-ui';

const mime = {
    xlsx: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    zip: 'application/zip;charset=utf-8',
};

/* 向后台发起请求获取需要下载的文件*/
export async function download(method, url, data, type) {
    if (method === 'get') {
        url = global.getRequestPath(url, data);
    }
    await axios({
        method: method,
        url: constant.apiUrl + url,
        data: data,
        responseType: 'blob',
        headers: cipher.getHeaders(),
    }).then((response) => {
        resolveBlob(response, mime[type]);
    });
}

/* 解析blob响应内容并下载*/
export function resolveBlob(response) {
    // 从header里面获取文件名
    const fileName = response.headers['file-name'];
    // 根据是否返回文件名判断下载成功
    if (!fileName) {
        Message.error('导出失败！');
        return;
    }
    // 获取请求返回的bolb
    const blob = new Blob([response.data], {type: mime.zip});
    // 创建a标签
    const aLink = document.createElement('a');
    aLink.style.display = 'none';
    aLink.download = decodeURI(fileName);
    aLink.href = URL.createObjectURL(blob);
    // 触发下载
    document.body.appendChild(aLink);
    aLink.click();
    document.body.removeChild(aLink);
}
