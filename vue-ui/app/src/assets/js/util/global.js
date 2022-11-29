const global = {};

/* 封装get请求路径（require('qs').stringify(data)）*/
global.getRequestPath = (url, data) => {
    if (url.charAt(url.length - 1) !== '?') {
        url += '?';
    }
    for (const key in data) {
        url += `${key}=${data[key]}&`;
    }
    return url.substr(0, url.length - 1);
};

/* 获取UUID*/
global.getUuid = () => 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : r & 0x3 | 0x8;
    return v.toString(16);
}).replace(/-/g, '');

/* 获取指定范围内的随机数*/
global.getNonce = (min, max) => Math.floor(Math.random() * (max - min + 1) + min);

/* 获取指定位数的随机字符串*/
global.getNoncestr = (number) => {
    const charStr = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    let noncestr = '';
    for (let i = 0; i < number; i++) {
        noncestr += charStr[global.getNonce(0, charStr.length - 1)];
    }
    return noncestr;
};

/* 根据键值对从数组重提取匹配对象*/
global.extractByKey = (array, value, key) => {
    for (const item of array) {
        if (item[key] === value) {
            return item;
        }
    }
    return {};
};

/* 图片链接转base64*/
global.convertImgToBase64 = (url, callback, outputFormat) => {
    let canvas = document.createElement('CANVAS');
    let ctx = canvas.getContext('2d');
    let img = new Image();
    img.crossOrigin = 'Anonymous';
    img.onload = function () {
        canvas.height = img.height;
        canvas.width = img.width;
        ctx.drawImage(img, 0, 0);
        const dataURL = canvas.toDataURL(outputFormat || 'image/png');
        callback.call(this, dataURL);
        canvas = null;
    };
    img.src = url;
};

/* 深度拷贝*/
global.deepClone = (object) => {
    const clone = JSON.stringify(object);
    const result = JSON.parse(clone);
    return result;
};

export default global;
