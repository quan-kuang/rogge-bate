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
global.extractByList = (listData, keyName, keyValue) => {
    for (const item of listData) {
        if (item[keyName] === keyValue) {
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
    return JSON.parse(clone);
};

/* 从树形结构的数据中广度遍历出指定key值*/
global.extractByTree = (treeData, keyName, keyValue) => {
    let stark = [];
    stark = stark.concat(treeData);
    while (stark.length) {
        const temp = stark.shift();
        if (temp.children) {
            stark = stark.concat(temp.children);
        }
        if (temp[keyName] === keyValue) {
            return temp;
        }
    }
};

/* 从树形结构的数据中模糊匹配指定key值*/
global.filterTree = (treeData, keyName, keyValue) => {
    if (!keyValue) {
        return treeData;
    }
    let result = [];
    let stark = [];
    stark = stark.concat(treeData);
    while (stark.length) {
        const temp = stark.shift();
        if (temp.children) {
            stark = stark.concat(temp.children);
        }
        if (temp[keyName].indexOf(keyValue) > -1) {
            delete temp['children'];
            result.push(temp);
        }
    }
    return result;
};

/* 对象数组根据字段排序*/
global.arySort = (ary, fields) => {
    const order = (left, right, fields) => {
        let field = fields.shift();
        if (field === undefined) {
            return 0;
        }
        return left[field] === right[field] ? order(left, right, fields) : left[field] < right[field] ? -1 : 1;
    };
    return ary.sort((left, right) => {
        return order(left, right, global.deepClone(fields));
    });
};

export default global;
