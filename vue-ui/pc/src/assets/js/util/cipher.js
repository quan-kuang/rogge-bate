import cryptoJs from 'crypto-js';
import JsEncrypt from 'jsencrypt';
import global from '@assets/js/util/global';
import format from '@assets/js/util/format';
import constant from '@assets/js/common/constant';
import storage from '@assets/js/config/storage';

const cipher = {};

// 公钥加密
const PUBLIC_KEY = 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCmdctCQ9ttXtSqby35XGHBXU38Pdvu7qxjxRExa76kaD5ffa5M7Xgq3rTTPPnGYqBm7lCP7wY/aaGT1fqVUn0XX8EQVsoGCRi3Iz+coP4miHBn6SmrXDATn1IcAYtEXws7xGgXgSs+zDobz191052g7rgkdfl3lcE4JLA08KpHPQIDAQAB';

// 私钥解密
const PRIVATE_KEY = 'MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKZ1y0JD221e1KpvLflcYcFdTfw92+7urGPFETFrvqRoPl99rkzteCretNM8+cZioGbuUI/vBj9poZPV+pVSfRdfwRBWygYJGLcjP5yg/iaIcGfpKatcMBOfUhwBi0RfCzvEaBeBKz7MOhvPX3XTnaDuuCR1+XeVwTgksDTwqkc9AgMBAAECgYA738DVu/ywUVzjVQxPVlhfKgWEBmgWgEkHtfmiMiQnlMtio51PVFdV7DdUh+k733vcqjnWG96+bmtM9rmkfkA503iNrdVPBjzY8x0DsaOmG6D33GvN6XTIo89oWRCcy/uCYv+o9OpxgVuXrifAlL8P+uxp7BflRnuz68YmOMa9DQJBAOl8YB8tUejFUCHq4E+FciGX+APrIzIVSt01AoCHYW6x6n7GcAypqa8S817DafH74pR0UxsXG7Hzp6QJ7NBXfusCQQC2guGakUfTWjLYefyzCzfV/BLreHoC3wVv0+yMvolqFbi3vFkjY64+7Rb63K0qu8ndPd5F1DGTXTQn4U4kuNh3AkBrsg/QMaJHYMO+cU03wNDNCADBJfNdBY87i1j1GfzqByynfzZt0NBQzcft3OsAT/PKEAHJTBZdNYsM0fsmekUzAkB1dbJASfPR4CENFSU/DZ83xm1ewkC0DFhxahC5W/8QGT6ycTNlBUglE21Qsil4cTcvfhvJERF+5+MjL2udUqFtAkBrM1OjM/5yCgrK6NlZxm497ET0zFGmHdXlV7gP/Gtmcr7BCvGC/zqW42DK4ePlvmmn9ftq7rsU9ibfRdXPiCCu';

/* RSA加密*/
cipher.encryptRSA = (message) => {
    let encrypt = new JsEncrypt();
    encrypt.setPublicKey(PUBLIC_KEY);
    return encrypt.encrypt(message);
};

/* RSA解密*/
cipher.decryptRSA = (message) => {
    let encrypt = new JsEncrypt();
    encrypt.setPrivateKey(PRIVATE_KEY);
    return encrypt.decrypt(message);
};

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
