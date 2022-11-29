package com.loyer.modules.system.enums;

/**
 * 验证码类型
 *
 * @author kuangq
 * @date 2020-11-14 13:08
 */
public enum CaptchaType {

    //文字验证码
    CODE("code"),

    //拼图验证码
    PUZZLE("puzzle");

    private final String value;

    CaptchaType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}