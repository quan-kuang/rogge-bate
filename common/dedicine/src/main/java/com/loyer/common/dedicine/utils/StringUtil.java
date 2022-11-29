package com.loyer.common.dedicine.utils;

import com.loyer.common.dedicine.constant.SystemConst;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;

/**
 * 字符串工具类
 *
 * @author kuangq
 * @date 2021-11-12 15:22
 */
public class StringUtil {

    /**
     * 编码
     *
     * @author kuangq
     * @date 2021-11-12 15:24
     */
    public static byte[] decode(String message) {
        return message.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 解码
     *
     * @author kuangq
     * @date 2019-12-24 13:09
     */
    public static String encode(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 16进制字符串转字节数组
     *
     * @author kuangq
     * @date 2019-12-09 13:54
     */
    @SneakyThrows
    public static byte[] hexDecode(String str) {
        int delta = 2;
        byte[] strBytes = decode(str);
        int length = strBytes.length;
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i < length; i = i + delta) {
            String tempStr = new String(strBytes, i, 2);
            bytes[i / 2] = (byte) Integer.parseInt(tempStr, 16);
        }
        return bytes;
    }

    /**
     * 字节数组转16进制字符串
     *
     * @author kuangq
     * @date 2019-12-09 13:53
     */
    public static String hexEncode(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        int index = 0;
        for (byte b : bytes) {
            chars[index++] = SystemConst.HEX_CHAR[b >>> 4 & 0xf];
            chars[index++] = SystemConst.HEX_CHAR[b & 0xf];
        }
        return new String(chars);
    }
}