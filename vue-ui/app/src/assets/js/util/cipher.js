import cryptoJs from 'crypto-js';
import global from '@assets/js/util/global';
import format from '@assets/js/util/format';
import constant from '@assets/js/common/constant';
import storage from '@assets/js/config/storage';

const cipher = {};

/* MD5加密*/
cipher.encryptMD5 = (message) => {
    return cryptoJs.MD5(message).toString().toLowerCase();
};

/* DES加密*/
cipher.encryptDES = (message) => {
    const keyHex = cryptoJs.enc.Utf8.parse(constant.secretKey);
    const option = {mode: cryptoJs.mode.ECB, padding: cryptoJs.pad.Pkcs7};
    const encrypted = cryptoJs.DES.encrypt(message, keyHex, option);
    return encrypted.ciphertext.toString();
};

/* DES解密*/
cipher.decryptDES = (message) => {
    message = {ciphertext: cryptoJs.enc.Hex.parse(message)};
    const keyHex = cryptoJs.enc.Utf8.parse(constant.secretKey);
    const option = {mode: cryptoJs.mode.ECB, padding: cryptoJs.pad.Pkcs7};
    const decrypted = cryptoJs.DES.decrypt(message, keyHex, option);
    return decrypted.toString(cryptoJs.enc.Utf8);
};

/* AES_1获取加密key*/
cipher.getKey = (password) => {
    let realKey = cryptoJs.SHA1(password);
    realKey = cryptoJs.SHA1(realKey).toString()
        .substring(0, 32);
    return cryptoJs.enc.Hex.parse(realKey);
};

/* AES_1加密*/
cipher.encryptAES1 = (message, password) => {
    if (message instanceof Object) {
        message = JSON.stringify(message);
    }
    let key = cipher.getKey(password);
    let option = {mode: cryptoJs.mode.ECB, padding: cryptoJs.pad.Pkcs7};
    let encrypt = cryptoJs.AES.encrypt(message, key, option);
    return encrypt.ciphertext.toString(cryptoJs.enc.Hex);
};

/* AES_1解密*/
cipher.decryptAES1 = (message, password) => {
    message = {ciphertext: cryptoJs.enc.Hex.parse(message)};
    const key = cipher.getKey(password);
    const option = {mode: cryptoJs.mode.ECB, padding: cryptoJs.pad.Pkcs7};
    const decrypt = cryptoJs.AES.decrypt(message, key, option);
    return decrypt.toString(cryptoJs.enc.Utf8);
};

/* AES2加密*/
cipher.encryptAES2 = (message, password) => {
    if (message instanceof Object) {
        message = JSON.stringify(message);
    }
    const iv = cryptoJs.enc.Utf8.parse(constant.secretKeyAES);
    const keyHex = cryptoJs.enc.Utf8.parse(password);
    const option = {iv: iv, mode: cryptoJs.mode.CBC, padding: cryptoJs.pad.Pkcs7};
    const encrypt = cryptoJs.AES.encrypt(message, keyHex, option);
    return encrypt.ciphertext.toString();
};

/* AES2解密*/
cipher.decryptAES2 = (message, password) => {
    message = cryptoJs.enc.Hex.parse(message);
    message = cryptoJs.enc.Base64.stringify(message);
    const iv = cryptoJs.enc.Utf8.parse(constant.secretKeyAES);
    const keyHex = cryptoJs.enc.Utf8.parse(password);
    const option = {iv: iv, mode: cryptoJs.mode.CBC, padding: cryptoJs.pad.Pkcs7};
    const decrypt = cryptoJs.AES.decrypt(message, keyHex, option);
    return decrypt.toString(cryptoJs.enc.Utf8);
};

/* 设置统一请求headers*/
cipher.getHeaders = () => {
    const noncestr = global.getUuid();
    const timestamp = new Date().getTime();
    const desEncryptStr = cipher.encryptDES(`${noncestr}&${timestamp}`);
    const md5EncryptStr = cipher.encryptMD5(desEncryptStr);
    const token = format.null(storage.getItem('token'));
    return {noncestr: noncestr, timestamp: timestamp, signature: md5EncryptStr, token: token};
};

export default cipher;
