package com.loyer.common.dedicine.utils;

import java.util.Base64;

/**
 * Base64加解密工具类
 *
 * @author kuangq
 * @date 2019-12-24 9:55
 */
public class Base64Util {

    /**
     * 编码
     *
     * @author kuangq
     * @date 2020-12-18 16:21
     */
    public static String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 解码
     *
     * @author kuangq
     * @date 2020-12-18 16:24
     */
    public static byte[] decode(String message) {
        return Base64.getDecoder().decode(message);
    }
}