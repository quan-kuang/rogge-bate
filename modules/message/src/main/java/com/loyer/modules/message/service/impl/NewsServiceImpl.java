package com.loyer.modules.message.service.impl;

import com.loyer.common.core.constant.PrefixConst;
import com.loyer.common.core.enums.RegExp;
import com.loyer.common.core.utils.common.CheckParamsUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.utils.GeneralUtil;
import com.loyer.common.redis.utils.RedisUtil;
import com.loyer.common.security.service.UserDetailsServiceImpl;
import com.loyer.modules.message.entity.MailParams;
import com.loyer.modules.message.entity.MessageParams;
import com.loyer.modules.message.service.NewsService;
import com.loyer.modules.message.utils.MailUtil;
import com.loyer.modules.message.utils.MessageUtil;
import com.loyer.modules.websocket.config.Websocket;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * @author kuangq
 * @title NewsServiceImpl
 * @description 消息模块ServiceImpl
 * @date 2021-05-30 21:28
 */
@Service
public class NewsServiceImpl implements NewsService {

    @Resource
    private MailUtil mailUtil;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private Websocket websocket;

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
        String random = getRandom(digit);
        messageParams.setTemplateParams(random);
        //发送短信
        ApiResult apiResult = MessageUtil.sendMessage(messageParams);
        //加入缓存
        if (apiResult.getFlag()) {
            redisUtil.setValue(PrefixConst.CAPTCHA + phone, random, 3 * 60L);
        }
        return apiResult;
    }

    /**
     * @param digit
     * @return java.lang.String
     * @author kuangq
     * @description 获取随机数
     * @date 2021-06-15 17:19
     */
    public static String getRandom(int digit) {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < digit; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }

    @SneakyThrows
    @Override
    public void selectRedisShakeLog(String sessionId) {
        File file = new File("C:/Intel/DevTools/Redis-Shake-v2.1.1/redis-shake.log");
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        while (true) {
            String line = randomAccessFile.readLine();
            if (line == null) {
                GeneralUtil.sleep(1000L);
                line = randomAccessFile.readLine();
                if (line == null) {
                    websocket.onClose(sessionId);
                    randomAccessFile.close();
                    return;
                }
            }
            if (!websocket.unicast(sessionId, line)) {
                websocket.onClose(sessionId);
                randomAccessFile.close();
            }
        }
    }
}