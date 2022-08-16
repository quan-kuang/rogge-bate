package com.loyer.common.dedicine.utils;

import java.util.Base64;

/**
 * @author kuangq
 * @title Base64Util
 * @description Base64加解密工具类
 * @date 2019-12-24 9:55
 */
public class Base64Util {

    /**
     * @param bytes
     * @return java.lang.String
     * @author kuangq
     * @description 编码
     * @date 2020-12-18 16:21
     */
    public static String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * @param message
     * @return byte[]
     * @author kuangq
     * @description 解码
     * @date 2020-12-18 16:24
     */
    public static byte[] decode(String message) {
        return Base64.getDecoder().decode(message);
    }
}