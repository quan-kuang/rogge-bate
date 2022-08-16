import axios from 'axios';
import constant from '@assets/js/common/constant';
import storage from '@assets/js/config/storage';
import cipher from '@assets/js/util/cipher';

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
        config.headers = cipher.getHeaders();
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
        }
        return response;
    },
    (error) => {
        // 请求超时
        if (error.message.includes('timeout')) {
            console.error('请求超时，请重试');
        }
        return Promise.reject(error);
    },
);

export default request;
