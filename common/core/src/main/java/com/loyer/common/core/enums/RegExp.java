package com.loyer.common.core.enums;

/**
 * 正则表达式的枚举类
 *
 * @author kuangq
 * @date 2019-12-12 17:40
 */
public enum RegExp {

    POSTCODE("^\\d{6}$", "邮政编码"),
    MAIL("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$", "邮箱"),
    NUMBER("^(\\-|\\+)?\\d+(\\.\\d+)?$", "实数"),
    CIPHER_STRENGTH("[-\\da-zA-Z`=\\\\ ;',./~!@#$%^&*()_+|{}:<>?]{6,40}", "密码强度"),
    UPPER_CASE(".*[A-Z].*", "全大写"),
    LOWER_CASE(".*[a-z].*", "全小写"),
    SPECIAL_CHARS(".*[-`=\\\\ ;',./~!@#$%^&*()_+|{}:<>?].*", "特殊字符"),
    PHONE("^1[3|4|5|7|8][0-9]\\d{8}$", "手机号"),
    ID_CARD("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$", "身份证号"),
    INTEGER("^[1-9]\\d*$", "非零正整数"),
    HOST("^((([1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}([1-9]?\\d|1\\d{2}|2[0-4]\\d|25[0-5])):([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$", "ip:port"),
    ;

    private final String regex;

    private final String declare;

    RegExp(String regex, String declare) {
        this.regex = regex;
        this.declare = declare;
    }

    public String getRegex() {
        return regex;
    }

    public String getDeclare() {
        return declare;
    }
}