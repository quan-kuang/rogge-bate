package com.loyer.common.core.utils.common;

import com.loyer.common.core.constant.SpecialCharsConst;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 字符串工具类
 *
 * @author kuangq
 * @date 2020-12-16 10:14
 */
public class StringUtil extends com.loyer.common.dedicine.utils.StringUtil {

    /**
     * 格式化驼峰式命名
     *
     * @author kuangq
     * @date 2020-07-20 16:08
     */
    public static String formatCamelCase(String value) {
        String[] strings = value.toLowerCase().split(SpecialCharsConst.UNDERLINE);
        StringBuilder stringBuilder = new StringBuilder(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            stringBuilder.append(firstUpper(strings[i]));
        }
        return String.valueOf(stringBuilder);
    }

    /**
     * 格式化下划线格式命名
     *
     * @author kuangq
     * @date 2022-03-09 13:32
     */
    public static String formatUnderline(String value) {
        int length = value.length();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char charAt = value.charAt(i);
            if (Character.isUpperCase(charAt)) {
                if (i > 0) {
                    stringBuilder.append(SpecialCharsConst.UNDERLINE);
                }
                stringBuilder.append(Character.toLowerCase(charAt));
                continue;
            }
            stringBuilder.append(charAt);
        }
        return stringBuilder.toString();
    }

    /**
     * 首字母大写
     *
     * @author kuangq
     * @date 2020-07-20 16:09
     */
    public static String firstUpper(String value) {
        char[] chars = value.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }

    /**
     * 首字母小写
     *
     * @author kuangq
     * @date 2020-07-20 16:09
     */
    public static String firstLower(String value) {
        char[] chars = value.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 压缩字符串
     *
     * @author kuangq
     * @date 2020-12-22 16:37
     */
    @SneakyThrows
    public static String compress(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        //创建一个新的字节数组输出流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //使用默认缓冲区大小创建新的输出流
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        //将字节写入此输出流
        gzipOutputStream.write(str.getBytes());
        //关闭流
        gzipOutputStream.close();
        //使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return byteArrayOutputStream.toString(StandardCharsets.ISO_8859_1.name());
    }

    /**
     * 解压字符串
     *
     * @author kuangq
     * @date 2020-12-22 16:37
     */
    @SneakyThrows
    public static String decompress(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        //创建一个新的字节数组输出流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //创建一个ByteArrayInputStream，使用buf作为其缓冲区数组
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes(StandardCharsets.ISO_8859_1));
        //使用默认缓冲区大小创建新的输入流
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        byte[] buffer = new byte[256];
        int n;
        //将未压缩数据读入字节数组
        while ((n = gzipInputStream.read(buffer)) >= 0) {
            //将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此 byte数组输出流
            byteArrayOutputStream.write(buffer, 0, n);
        }
        //使用指定的charsetName，通过解码字节将缓冲区内容转换为字符串
        return byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
    }
}