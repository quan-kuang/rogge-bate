package com.loyer.modules.message.service;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.message.entity.MailParams;
import com.loyer.modules.message.entity.MessageParams;

import java.util.List;

/**
 * 消息模块Service
 *
 * @author kuangq
 * @date 2021-05-30 21:28
 */
public interface NewsService {

    ApiResult sendMail(MailParams mailParams);

    ApiResult sendLoginInformMail(List<String> messageList);

    ApiResult sendCaptcha(MessageParams messageParams);

    ApiResult printConsoleLog(Object message);
}