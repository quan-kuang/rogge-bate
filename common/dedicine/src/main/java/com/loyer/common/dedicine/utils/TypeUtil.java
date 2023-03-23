package com.loyer.common.dedicine.utils;

import org.springframework.util.Assert;

/**
 * 类型转换
 *
 * @author kuangq
 * @date 2023-3-23 14:01
 */
public class TypeUtil {

    /**
     * 类型强转
     *
     * @author kuangq
     * @date 2023-03-23 14:03
     */
    @SuppressWarnings("unchecked")
    public static <U, T> T convert(U object) {
        Assert.notNull(object, "转换对象不能为空");
        return (T) object;
    }
}