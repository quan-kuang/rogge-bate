import axios from 'axios';
import constant from '@assets/js/common/constant';
import router from '@assets/js/config/router';
import storage from '@assets/js/config/storage';
import cipher from '@assets/js/util/cipher';
import {Notification} from 'element-ui';
import global from '@assets/js/util/global';
import websocket from '@assets/js/util/websocket';

// 创建axios实例
const request = axios.create({
    baseURL: constant.apiUrl, // 请求URL公共部分
    timeout: 15000, // 超时时长
});

// 请求携带cookie
request.defaults.withCredentials = true;

// 添加请求拦截器
request.interceptors.request.use(
    (config) => {
        const headers = cipher.getHeaders();
        if (config.url.includes('openWebsocket')) {
            const params = global.deepClone(headers);
            const sessionId = global.getUuid();
            params.sessionId = sessionId;
            params.serviceName = config.url.substring(0, config.url.indexOf('/'));
            if (websocket.openWebsocket(params) === null) {
                return null;
            }
            config.url = config.url.replace('openWebsocket', `sessionId=${sessionId}`);
        }
        config.headers = Object.assign(config.headers, headers);
        return config;
    },
    (error) => {
        return Promise.reject(error);
    },
);

// 添加响应拦截器
request.interceptors.response.use(
    (response) => {
        // 会话过期，跳转重新登录
        if (response.data.code === 1099) {
            storage.removeItem('token');
            storage.removeItem('user');
            router.push('/login');
        }
        return response.data;
    },
    (error) => {
        let title, message;
        if ([500, 502].includes(error.response.status)) {
            router.push('/500');
            return;
        } else if ([404].includes(error.response.status)) {
            title = 'error';
            message = error.response.statusText;
        } else if (error.message.includes('timeout')) {
            title = 'error';
            message = error.message;
        } else {
            const errorData = error.response.data;
            title = errorData.error;
            message = errorData.message;
        }
        Notification.error({title: title, message: message});
        return Promise.reject(error);
    },
);

export default request;
