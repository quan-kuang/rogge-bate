const cryptoJs = require('crypto-js');

const cipher = {};

// 私人密匙value值
const secretKey = '931851631';

/* 获取UUID*/
cipher.getUuid = () => 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : r & 0x3 | 0x8;
    return v.toString(16);
}).replace(/-/g, '');

/* MD5加密*/
cipher.encryptMD5 = (message) => {
    return cryptoJs.MD5(message).toString().toLowerCase();
};

/* DES加密*/
cipher.encryptDES = (message) => {
    const keyHex = cryptoJs.enc.Utf8.parse(secretKey);
    const option = {mode: cryptoJs.mode.ECB, padding: cryptoJs.pad.Pkcs7};
    const encrypted = cryptoJs.DES.encrypt(message, keyHex, option);
    return encrypted.ciphertext.toString();
};

/* DES解密*/
cipher.decryptDES = (message) => {
    message = {ciphertext: cryptoJs.enc.Hex.parse(message)};
    const keyHex = cryptoJs.enc.Utf8.parse(secretKey);
    const option = {mode: cryptoJs.mode.ECB, padding: cryptoJs.pad.Pkcs7};
    const decrypted = cryptoJs.DES.decrypt(message, keyHex, option);
    return decrypted.toString(cryptoJs.enc.Utf8);
};

/* 设置统一请求headers*/
cipher.getHeaders = (token) => {
    const noncestr = cipher.getUuid();
    const timestamp = new Date().getTime();
    const desEncryptStr = cipher.encryptDES(`${noncestr}&${timestamp}`);
    const md5EncryptStr = cipher.encryptMD5(desEncryptStr);
    return {noncestr: noncestr, timestamp: timestamp, signature: md5EncryptStr, token: token};
};

module.exports = cipher;
