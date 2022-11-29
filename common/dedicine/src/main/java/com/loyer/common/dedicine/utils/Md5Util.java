package com.loyer.common.dedicine.utils;

import lombok.SneakyThrows;

import java.security.MessageDigest;

/**
 * MD5加密
 *
 * @author kuangq
 * @date 2019-08-12 10:14
 */
public class Md5Util {

    /**
     * MD5加密
     *
     * @author kuangq
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