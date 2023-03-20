package com.loyer.common.core.constant;

/**
 * 前缀常量
 *
 * @author kuangq
 * @date 2020-11-04 22:42
 */
public interface PrefixConst {

    //缓存用户前缀
    String LOGIN_USER = "system:loginUser:";

    //缓存在线用户信息前缀
    String ONLINE_USER = "system:onLineUser:";

    //缓存百度接口的access_token前缀
    String BAIDU_AUTH_TOKEN = "baidu:authToken:";

    //缓存接口限流的前缀
    String THROTTLING = "throttling:";

    //缓存微信接口apiTicket的前缀
    String WE_CHAT_API_TICKET = "weChat:apiTicket:";

    //缓存微信接口apiTicket的前缀
    String WE_CHAT_ACCESS_TOKEN = "weChat:accessToken:";

    //缓存微信接口apiTicket的前缀
    String WE_CHAT_AUTH_USER_INFO = "weChat:authUserInfo:";

    //缓存验证码的前缀
    String CAPTCHA = "system:captcha:";

    //缓存腾讯云接口BizToken的前缀
    String TENCENT_BIZ_TOKEN = "tencent:bizToken:";

    //微信消息缓存前缀
    String WE_CHAT_MESSAGE = "weChat:message:";
}