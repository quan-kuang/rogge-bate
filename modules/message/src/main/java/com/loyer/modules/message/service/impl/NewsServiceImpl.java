package com.loyer.modules.message.service.impl;

import com.alibaba.fastjson.JSON;
import com.loyer.common.core.constant.PrefixConst;
import com.loyer.common.core.entity.ConsoleLog;
import com.loyer.common.core.enums.RegExp;
import com.loyer.common.core.utils.common.CheckParamsUtil;
import com.loyer.common.core.utils.common.ExecutorUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.utils.GeneralUtil;
import com.loyer.common.rabbitmq.enums.MessageTemplate;
import com.loyer.common.rabbitmq.utils.SendMessageUtil;
import com.loyer.common.redis.utils.CacheUtil;
import com.loyer.common.security.service.UserDetailsServiceImpl;
import com.loyer.modules.message.entity.MailParams;
import com.loyer.modules.message.entity.MessageParams;
import com.loyer.modules.message.service.NewsService;
import com.loyer.modules.message.utils.MailUtil;
import com.loyer.modules.message.utils.MessageUtil;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * 消息模块ServiceImpl
 *
 * @author kuangq
 * @date 2021-05-30 21:28
 */
@Service
public class NewsServiceImpl implements NewsService {

    @Resource
    private MailUtil mailUtil;

    @Resource
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public ApiResult sendMail(MailParams mailParams) {
        return mailUtil.sendMail(mailParams);
    }

    @Override
    public ApiResult sendLoginInformMail(List<String> messageList) {
        MailParams mailParams = new MailParams();
        mailParams.setSender("rogbet");
        mailParams.setReceiver("931851631@qq.com");
        mailParams.setSubject("登录通知");
        mailParams.setBody(getBody(messageList));
        return mailUtil.sendMail(mailParams);
    }

    @SneakyThrows
    private String getBody(List<String> messageList) {
        final String src = "/static/html/mail.html";
        InputStream inputStream = this.getClass().getResourceAsStream(src);
        String html = IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8);
        return String.format(null, html, messageList.toArray());
    }

    @Override
    public ApiResult sendCaptcha(MessageParams messageParams) {
        //入参手机号格式校验
        String phone = messageParams.getPhoneNumbers();
        if (!CheckParamsUtil.regularCheck(RegExp.PHONE, phone)) {
            return ApiResult.failure("手机号格式错误", phone);
        }
        //根据手机号查询用户信息
        userDetailsService.selectUser("phone", phone);
        //截取入参第一个数字设置生成几位随机数
        String firstNumber = messageParams.getTemplateParams().substring(0, 1);
        int digit = CheckParamsUtil.regularCheck(RegExp.INTEGER, firstNumber) ? Integer.parseInt(firstNumber) : 6;
        //生成指定位随机数验证码
        String random = GeneralUtil.getRandom(digit);
        messageParams.setTemplateParams(random);
        //发送短信
        ApiResult apiResult = MessageUtil.sendMessage(messageParams);
        //加入缓存
        if (apiResult.getFlag()) {
            CacheUtil.VALUE.set(PrefixConst.CAPTCHA + phone, random, 3 * 60L);
        }
        return apiResult;
    }

    /**
     * 打印控制台日志
     *
     * @author kuangq
     * @date 2022-08-18 18:00
     */
    @Override
    public ApiResult printConsoleLog(Object message) {
        ExecutorUtil.task(() -> saveConsoleLogToCache(message));
        SendMessageUtil.send(MessageTemplate.CONSOLE_LOG, message);
        return ApiResult.success();
    }

    /**
     * 保存控制台日志到缓存
     *
     * @author kuangq
     * @date 2022-08-18 18:10
     */
    public void saveConsoleLogToCache(Object message) {
        ConsoleLog consoleLog = JSON.parseObject(JSON.toJSONString(message), ConsoleLog.class);
        String key = String.format("%s%s", com.loyer.common.redis.constant.PrefixConst.CONSOLE_LOG, consoleLog.getSessionId());
        CacheUtil.LIST.rPush(key, consoleLog.getActive() ? consoleLog.getContent() : "finished");
        CacheUtil.KEY.expire(key, 3600 * 24 * 3L);
    }
}