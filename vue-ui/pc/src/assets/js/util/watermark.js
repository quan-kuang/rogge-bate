const watermark = {};

/* 水印监听器*/
watermark.observer = null;

/* 设置背景水印*/
watermark.addWatermark = function (object) {
    // 默认设置
    let settings = {
        className: 'watermark', // 类名
        content: '', // 水印内容
        font: '微软雅黑', // 水印字体
        fontsize: '15px', // 水印字体大小
        color: '#aaa', // 水印字体颜色
        width: 100, // 水印宽度
        height: 50, // 水印长度
        alpha: 0.2, // 水印透明度
        angle: 30, // 水印倾斜度
        rows: 15, // 水印行数
        cols: 15, // 水印列数
        x_axis: 100, // 水印起始x轴坐标
        y_axis: 100, // 水印起始Y轴坐标
        x_axis_interval: 100, // 水印x轴间隔
        y_axis_interval: 50, // 水印y轴间隔
    };
    // 传入参数设置
    if (arguments.length === 1 && typeof arguments[0] === 'object') {
        const params = arguments[0] || {};
        Object.assign(settings, params);
    }
    // 判断是否已经存在水印
    if (document.body.getElementsByClassName(settings.className).length > 0) {
        return;
    }
    // 获取页面最大宽度
    const page_width = document.body.clientWidth - settings.width - settings.x_axis_interval;
    // 获取页面最大高度
    const page_height = document.body.clientHeight - settings.height - settings.y_axis_interval;
    // 如果将水印列数设置为0，或水印列数设置过大，超过页面最大宽度，则重新计算水印列数和水印x轴间隔
    if (settings.cols === 0 || settings.x_axis + settings.width * settings.cols + settings.x_axis_interval * (settings.cols - 1) > page_width) {
        settings.cols = (page_width - settings.x_axis + settings.x_axis_interval) / (settings.width + settings.x_axis_interval);
        settings.x_axis_interval = (page_width - settings.x_axis - settings.width * settings.cols) / (settings.cols - 1);
    }
    // 如果将水印行数设置为0，或水印行数设置过大，超过页面最大长度，则重新计算水印行数和水印y轴间隔
    if (settings.rows === 0 || settings.y_axis + settings.height * settings.rows + settings.y_axis_interval * (settings.rows - 1) > page_height) {
        settings.rows = (page_height - settings.y_axis + settings.y_axis_interval) / (settings.height + settings.y_axis_interval);
        settings.y_axis_interval = (page_height - settings.y_axis - settings.height * settings.rows) / (settings.rows - 1);
    }
    // 添加水印
    let nodes = document.createDocumentFragment();
    for (let i = 0; i < settings.rows; i++) {
        let y = settings.y_axis + (settings.y_axis_interval + settings.height) * i;
        for (let j = 0; j < settings.cols; j++) {
            let x = settings.x_axis + (settings.width + settings.x_axis_interval) * j;
            let node = document.createElement('div');
            node.id = 'node' + i + j;
            node.className = settings.className;
            node.appendChild(document.createTextNode(settings.content));
            // 设置水印div倾斜显示
            node.style.visibility = '';
            node.style.left = x + 'px';
            node.style.top = y + 'px';
            node.style.zIndex = '666';
            node.style.overflow = 'hidden';
            node.style.position = 'fixed';
            node.style.transform = 'rotate(-' + settings.angle + 'deg)';
            node.style.OTransform = 'rotate(-' + settings.angle + 'deg)';
            node.style.MsTransform = 'rotate(-' + settings.angle + 'deg)';
            node.style.MozTransform = 'rotate(-' + settings.angle + 'deg)';
            node.style.WebkitTransform = 'rotate(-' + settings.angle + 'deg)';
            // 让水印不遮挡页面的点击事件
            node.style.display = 'block';
            node.style.textAlign = 'center';
            node.style.pointerEvents = 'none';
            node.style.color = settings.color;
            node.style.opacity = settings.alpha;
            node.style.fontFamily = settings.font;
            node.style.fontSize = settings.fontsize;
            node.style.width = settings.width + 'px';
            node.style.height = settings.height + 'px';
            node.style.lineHeight = settings.height + 'px';
            nodes.appendChild(node);
        }
    }
    document.body.appendChild(nodes);
    // 防止水印被篡改
    this.recoverWatermark();
};

/* 删除背景水印*/
watermark.deleteWatermark = function (className) {
    this.observer && this.observer.disconnect();
    className = className || 'watermark';
    while (document.body.getElementsByClassName(className).length > 0) {
        document.body.getElementsByClassName(className)[0].remove();
    }
};

/* 恢复背景水印*/
watermark.recoverWatermark = function () {
    // 添加MutationObserver监听DOM结构变化，防止用户通过控制台修改样式去除水印
    const observer = new MutationObserver((mutationRecords) => {
        // 避免恢复水印时造成递归，每次先断开监听
        observer.disconnect();
        for (let mutationRecord of mutationRecords) {
            // 水印被删除
            if (mutationRecord.type === 'childList' && mutationRecord.removedNodes.length > 0) {
                for (let removedNode of mutationRecord.removedNodes) {
                    if (removedNode.className === 'watermark') {
                        document.body.appendChild(removedNode);
                    } else if (mutationRecord.target.className === 'watermark') {
                        mutationRecord.target.innerText = removedNode.data;
                        document.body.appendChild(mutationRecord.target);
                    }
                }
            }
            // 水印属性被篡改
            if (mutationRecord.type === 'attributes' && mutationRecord.target.className === 'watermark') {
                mutationRecord.target.style = mutationRecord.oldValue;
            }
            // 水印文本值被篡改
            if (mutationRecord.type === 'characterData' && mutationRecord.target.parentElement && mutationRecord.target.parentElement.className === 'watermark') {
                mutationRecord.target.parentElement.innerText = mutationRecord.oldValue;
            }
        }
        this.recoverWatermark();
    });
    // 添加观察监听
    const target = document.body;
    const options = {
        childList: true, // 观察目标节点的子节点的新增和删除
        attributes: true, // 观察目标节点的属性变化
        characterData: true, // 观察目标节点的文本内容变化
        subtree: true, // 观察目标节点的所有后代节点的上述三种变化
        attributeOldValue: true, // 在attributes属性已经设为true的前提下，将变化前的属性值记录下来
        characterDataOldValue: true, // 在characterData属性已经设为true的前提下，将变化前的文本内容记录下来
    };
    observer.observe(target, options);
    this.observer = observer;
};

export default watermark;
