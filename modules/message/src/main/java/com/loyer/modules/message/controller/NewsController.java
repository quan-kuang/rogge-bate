package com.loyer.modules.message.controller;

import com.loyer.common.apis.server.MessageServer;
import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.core.control.BaseControl;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.rabbitmq.enums.MessageTemplate;
import com.loyer.common.rabbitmq.utils.SendMessageUtil;
import com.loyer.common.security.annotation.ThrottlingAnnotation;
import com.loyer.modules.message.entity.MailParams;
import com.loyer.modules.message.entity.MessageParams;
import com.loyer.modules.message.service.NewsService;
import com.loyer.modules.message.utils.MessageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 消息Controller
 *
 * @author kuangq
 * @date 2021-03-03 18:01
 */
@Api(tags = "消息模块")
@RestController
@RequestMapping("news")
public class NewsController extends BaseControl {

    @Resource
    private NewsService newsService;

    public NewsController(MessageServer messageServer) {
        super(messageServer);
    }

    @ThrottlingAnnotation(count = 1000)
    @ApiOperation("保存操作日志")
    @PostMapping("saveOperateLog")
    public ApiResult saveOperateLog(@RequestBody Object message) {
        SendMessageUtil.send(MessageTemplate.OPERATION_LOG, message);
        return ApiResult.success();
    }

    @ThrottlingAnnotation(count = 100)
    @ApiOperation("保存调度日志")
    @PostMapping("saveCrontabLog")
    public ApiResult saveCrontabLog(@RequestBody Object message) {
        SendMessageUtil.send(MessageTemplate.CRONTAB_LOG, message);
        return ApiResult.success();
    }

    @ThrottlingAnnotation(count = -1)
    @ApiOperation("打印控制台日志")
    @PostMapping("printConsoleLog")
    public ApiResult printConsoleLog(@RequestBody Object message) {
        return newsService.printConsoleLog(message);
    }

    @OperateLogAnnotation
    @ApiOperation("发送邮件")
    @PostMapping("sendMail")
    public ApiResult sendMail(@Validated @RequestBody MailParams mailParams) {
        return newsService.sendMail(mailParams);
    }

    @OperateLogAnnotation
    @ApiOperation("发送登录通知邮件")
    @PostMapping("sendLoginInformMail")
    public ApiResult sendLoginInformMail(@RequestBody List<String> messageList) {
        return newsService.sendLoginInformMail(messageList);
    }

    @OperateLogAnnotation
    @ApiOperation("发送短信验证码")
    @PostMapping("sendCaptcha")
    public ApiResult sendCaptcha(@Validated @RequestBody MessageParams messageParams) {
        return newsService.sendCaptcha(messageParams);
    }

    @OperateLogAnnotation
    @ApiOperation("发送短信消息")
    @PostMapping("sendMessage")
    public ApiResult sendMessage(@Validated @RequestBody MessageParams messageParams) {
        return MessageUtil.sendMessage(messageParams);
    }
}