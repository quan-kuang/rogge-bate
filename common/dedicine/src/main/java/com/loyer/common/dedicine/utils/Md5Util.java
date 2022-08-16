package com.loyer.common.dedicine.utils;

import lombok.SneakyThrows;

import java.security.MessageDigest;

/**
 * @author kuangq
 * @title Md5Util
 * @description MD5加密
 * @date 2019-08-12 10:14
 */
public class Md5Util {

    /**
     * @param message
     * @return java.lang.String
     * @author kuangq
     * @description MD5加密
     * @date 2019-12-09 13:53
     */
    @SneakyThrows
    public static String encrypt(String message) {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(StringUtil.decode(message));
        byte[] bytes = messageDigest.digest();
        return StringUtil.hexEncode(bytes);
    }
}