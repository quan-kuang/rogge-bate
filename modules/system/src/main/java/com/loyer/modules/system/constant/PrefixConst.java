package com.loyer.modules.system.constant;

/**
 * 前缀常量
 *
 * @author kuangq
 * @date 2021-11-23 16:17
 */
public interface PrefixConst extends com.loyer.common.core.constant.PrefixConst {

    //缓存系统常量前缀
    String CONSTANT = "system:constant";

    //微信报警接收人
    String WECHAT_ALRAM_USERS = "wechat:alram:users";

    //发送登录通知邮件的缓存KEY
    String SEND_LOGIN_INFORM_MAIL_KEY = "sendLoginInformMail";

    //微信报警模板ID的缓存KEY
    String WECHAT_ALARM_TEMPLATE = "wechatAlarmTemplate";
}