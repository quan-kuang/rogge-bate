/* eslint-disable no-unused-vars */
// noinspection ES6UnusedImports
import button from '@assets/css/button.css';
import {Notification} from 'element-ui';
import constant from '@assets/js/common/constant';

const websocket = {};

/* 建立websocket链接*/
websocket.openWebsocket = function (params) {
    let webSocket;
    const sessionId = params.sessionId;
    // 判断当前浏览器是否支持Websocket
    if ('WebSocket' in window) {
        try {
            const protocol = constant.environment ? 'wss' : 'ws';
            const serviceName = params.serviceName || 'message';
            const host = constant.apiUrl.substr(constant.apiUrl.indexOf('//') + 2);
            const url = `${protocol}://${host}${serviceName}/websocket/${params.noncestr}/${params.signature}/${params.timestamp}/${params.sessionId}`;
            webSocket = new WebSocket(url);
        } catch (e) {
            Notification.error({title: 'new WebSocket', message: e.toString()});
            return;
        }
    } else {
        Notification.error({title: 'websocket', message: 'Not Support WebSocket'});
        return;
    }
    // 连接成功建立的回调方法
    webSocket.onopen = function () {
        // 创建模态弹窗
        websocket.createDialog(sessionId, this);
        webSocket.send(JSON.stringify({content: 'start......'}));
    };
    // 连接关闭的回调方法
    webSocket.onclose = function () {
        webSocket.close();
    };
    // 连接发生错误的回调方法
    webSocket.onerror = function () {
        Notification.error({title: 'onerror', message: 'websocket connection fail'});
    };
    // 接收到消息的回调方法
    webSocket.onmessage = function (event) {
        websocket.consoleOutput(sessionId, event.data);
    };
    // 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常
    webSocket.onbeforeunload = function () {
        webSocket.onclose();
    };
    return webSocket;
};

/* 新建一个模态弹窗*/
websocket.createDialog = function (sessionId, webSocket) {
    // 创建模态窗
    const modalDialog = document.createElement('div');
    modalDialog.style.cssText = 'position: fixed;top: 0;left: 0;width: 100%;height: 100%;z-index: 888;background-color: rgba(0, 0, 0, 0.4);';
    // 创建窗体
    const container = document.createElement('div');
    container.style.cssText = 'position: fixed;top: 0;left: 0;bottom: 0;right: 0;width: 50%;margin: 5% auto;z-index: 888;';
    // 创建标题栏
    const header = document.createElement('div');
    header.style.cssText = 'background-color: rgb(242, 242, 242);height: 50px;text-align: center;';
    // 创建标题
    const title = document.createElement('span');
    title.innerHTML = '任务执行进度控制台';
    title.style.cssText = 'margin-left: 35;line-height: 50px;color: grey;font-size: 18px;font-weight: bold;user-select: none;';
    // 创建右侧关闭按钮
    const button = document.createElement('div');
    button.setAttribute('class', 'checkbox-btn checkbox-btn-close');
    button.setAttribute('title', '关闭');
    button.innerHTML = "<input type='checkbox' id='checkbox-btn' name='check' value='' checked /><label for='checkbox-btn' />";
    // 绑定鼠标点击关闭事件
    button.addEventListener('click', () => {
        modalDialog.parentNode.removeChild(modalDialog);
        webSocket.close();
    });
    // 创建自动滚动复选框
    const autoScrollCheckbox = document.createElement('div');
    autoScrollCheckbox.setAttribute('class', 'checkbox-btn');
    autoScrollCheckbox.setAttribute('title', '自动滚动');
    autoScrollCheckbox.innerHTML = `<input type='checkbox' id='checkbox${sessionId}' name='check' value='' checked /><label for='checkbox${sessionId}' />`;
    // 创建模态框主体
    const main = document.createElement('div');
    main.setAttribute('class', 'console');
    main.setAttribute('id', 'console' + sessionId);
    const height = Math.floor(document.body.clientHeight * 0.75);
    main.style.cssText = `background-color: rgb(30, 34, 45);height: ${height}px;overflow: auto;color: white;`;
    // 将元素添加到body中
    header.appendChild(title);
    header.appendChild(button);
    header.appendChild(autoScrollCheckbox);
    container.appendChild(header);
    container.appendChild(main);
    modalDialog.appendChild(container);
    document.body.appendChild(modalDialog);
};

/* 控制台输出*/
websocket.consoleOutput = function (sessionId, message) {
    const console = document.getElementById('console' + sessionId);
    if (!console) {
        return;
    }
    const p = document.createElement('p');
    p.innerHTML = message;
    console.appendChild(p);
    const input = document.getElementById('checkbox' + sessionId);
    if (!!input && input.checked) {
        console.scrollTop = console.scrollHeight;
    }
};

export default websocket;
