import global from '@assets/js/util/global';

const format = {};

/* 封装Oracle中DECODE函数else值默认为''*/
format.decode = function () {
    const length = arguments.length;
    const count = length % 2 === 0 ? length / 2 - 1 : parseInt(length / 2);
    for (let i = 0; i < count; i++) {
        if (arguments[0] === arguments[2 * i + 1]) {
            return arguments[2 * i + 2];
        }
    }
    return length % 2 === 0 ? arguments[length - 1] : '';
};

/* 格式化null值*/
format.null = (value) => {
    if (value === null) {
        return '';
    }
    return value;
};

/* 格式化性别*/
format.sex = (value) => {
    return format.decode(value, '0', '女', '1', '男', '-');
};

/* 字符串转布尔值*/
format.toBool = (value) => {
    if ((/^true$/i).test(value)) {
        return true;
    }
    if ((/^false$/i).test(value)) {
        return false;
    }
    return null;
};

/* 手机号脱敏*/
format.phone = (value) => {
    if (!value) {
        return '';
    }
    const regexp = /(\d{3})\d*(\d{4})/;
    return value.replace(regexp, '$1****$2');
};

/* 姓名脱敏*/
format.name = (value) => {
    if (!value) {
        return '';
    } else if (value.length < 3) {
        return value.substr(0, 1) + '*';
    } else if (value.length === 3) {
        return value.substr(0, 1) + '**';
    } else if (value.length > 3) {
        return value.substr(0, 2) + '**';
    }
};

/* 获取当前时间并格式化*/
format.date = (timestamp, format) => {
    try {
        const date = new Date(timestamp);
        const time = {
            'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
            'm+': date.getMonth() + 1, // 月份
            'd+': date.getDate(), // 日
            'h+': date.getHours(), // 小时
            'M+': date.getMinutes(), // 分
            's+': date.getSeconds(), // 秒
            'S': date.getMilliseconds(), // 毫秒
        };
        if (/(y+)/.test(format)) {
            format = format.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
        }
        for (let item in time) {
            if (new RegExp('(' + item + ')').test(format)) {
                format = format.replace(RegExp.$1, RegExp.$1.length === 1 ? time[item] : ('00' + time[item]).substr(('' + time[item]).length));
            }
        }
        return format;
    } catch (e) {
        console.log('cusFormat异常：' + e);
    }
};

/* 格式化list转换成树状结构*/
format.list = (list, root, id, parentId) => {
    // 设置根节点的默认值/主键/上级ID
    root = root || 'root';
    id = id || 'uuid';
    parentId = parentId || 'parentId';
    // 对源数据深度克隆
    const cloneList = global.deepClone(list);
    // 遍历所有项
    const treeList = cloneList.filter((parent) => {
        // 匹配下级
        let item = cloneList.filter((children) => {
            // 返回每一项的子级数组
            return parent[id] === children[parentId];
        });
        // 赋值children
        item.length > 0 ? parent.children = item : null;
        // 返回上级
        return parent[parentId] === root;
    });
    // 返回树状结构的list
    return treeList.length > 0 ? treeList : list;
};

/* 格式化树状结构的菜单数据转换成list*/
format.tree = (tree, list, parentPath) => {
    list = list || [];
    parentPath = parentPath || '';
    tree.forEach((node) => {
        if (node.type === '1') {
            list.push({title: node.title, uuid: node.uuid, path: '/' + parentPath + node.path});
        }
        if (!!node.children && node.children.length > 0) {
            format.tree(node.children, list, parentPath + node.path + '/');
        }
    });
    return list;
};

/* blob转base64*/
format.blob = (blob, callback) => {
    let fileReader = new FileReader();
    fileReader.readAsDataURL(blob);
    fileReader.onload = (response) => {
        callback(response.target.result);
    };
};

/* base64转blob*/
format.base64 = (base64) => {
    let bytes;
    // base64解码
    if (base64.split(',')[0].indexOf('base64') >= 0) {
        bytes = atob(base64.split(',')[1]);
    } else {
        bytes = unescape(base64.split(',')[1]);
    }
    // mime类型
    let mime = base64.split(',')[0].split(':')[1].split(';')[0];
    // 创建视图
    let uint8Array = new Uint8Array(bytes.length);
    for (let i = 0; i < bytes.length; i++) {
        uint8Array[i] = bytes.charCodeAt(i);
    }
    // 生成bolb对象
    return new Blob([uint8Array], {
        type: mime,
    });
};

format.sortObj = (data) => {
    // obj转map
    const map = new Map();
    for (let key in data) {
        map.set(key, data[key]);
    }
    // map转array根据key排序
    const array = Array.from(map);
    array.sort(function (a, b) {
        return a[0].localeCompare(b[0]);
    });
    // map转obj
    let object = Object.create(null);
    for (let [k, v] of new Map(array)) {
        object[k] = v;
    }
    return object;
};

export default format;
