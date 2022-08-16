package com.loyer.modules.system.enums;

/**
 * @author kuangq
 * @title CaptchaType
 * @description 验证码类型
 * @date 2020-11-14 13:08
 */
public enum CaptchaType {

    //文字验证码
    CODE("code"),

    //拼图验证码
    PUZZLE("puzzle");

    private final String value;

    public String value() {
        return this.value;
    }

    CaptchaType(String value) {
        this.value = value;
    }
}