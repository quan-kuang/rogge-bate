package com.loyer.common.core.enums;

/**
 * 时间类型
 *
 * @author kuangq
 * @date 2020-12-07 22:16
 */
public enum DatePattern {

    YM_1("yyyy-MM"),
    YMD_1("yyyy-MM-dd"),
    YMD_HM_1("yyyy-MM-dd HH:mm"),
    YMD_HMS_1("yyyy-MM-dd HH:mm:ss"),
    YMD_HMSS_1("yyyy-MM-dd HH:mm:ss:SSS"),

    YM_2("yyyyMM"),
    YMD_2("yyyyMMdd"),
    YMD_HM_2("yyyyMMdd HH:mm"),
    YMD_HMS_2("yyyyMMdd HH:mm:ss"),
    YMD_HMSS_2("yyyyMMdd HH:mm:ss:SSS"),

    YM_3("yyyy/MM"),
    YMD_3("yyyy/MM/dd"),
    YMD_HM_3("yyyy/MM/dd HH:mm"),
    YMD_HMS_3("yyyy/MM/dd HH:mm:ss"),
    YMD_HMSS_3("yyyy/MM/dd HH:mm:ss:SSS"),
    ;

    private final String pattern;

    DatePattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}