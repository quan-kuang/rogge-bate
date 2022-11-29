package com.loyer.modules.message.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 板块
 *
 * @author kuangq
 * @date 2021-09-16 21:06
 */
public enum Plate {

    //全部
    ALL("m:0+t:6+f:!2,m:0+t:7+f:!2,m:0+t:13+f:!2,m:0+t:80+f:!2,m:1+t:2+f:!2,m:1+t:3+f:!2,m:1+t:23+f:!2"),

    //沪深A股
    HSA("m:0+t:6+f:!2,m:0+t:13+f:!2,m:0+t:80+f:!2,m:1+t:2+f:!2,m:1+t:23+f:!2"),

    //沪市A股
    HA("m:1+t:2+f:!2,m:1+t:23+f:!2"),

    //科创板
    KC("m:1+t:23+f:!2"),

    //深市A股
    SA("m:0+t:6+f:!2,m:0+t:13+f:!2,m:0+t:80+f:!2"),

    //创业板
    CY("m:0+t:80+f:!2"),

    //沪市B股
    HB("m:1+t:3+f:!2"),

    //深市B股
    SB("m:0+t:7+f:!2");

    private final String value;

    public String value() {
        return this.value;
    }

    Plate(String value) {
        this.value = value;
    }

    public static String getValue(String code) {
        if (StringUtils.isBlank(code) || Arrays.stream(Plate.values()).noneMatch(item -> item.name().equalsIgnoreCase(code))) {
            return ALL.value;
        }
        return Plate.valueOf(code.toUpperCase()).value();
    }
}