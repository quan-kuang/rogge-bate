package com.loyer.common.apis.server;

import com.loyer.common.apis.demote.MessageDemote;
import com.loyer.common.dedicine.entity.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 消息服务
 *
 * @author kuangq
 * @date 2021-03-03 19:18
 */
@FeignClient(name = "MESSAGE", fallbackFactory = MessageDemote.class)
public interface MessageServer {

    /*发送保存接口调用日志的消息*/
    @PostMapping("news/saveOperateLog")
    ApiResult saveOperateLog(@RequestBody Object message);

    /*发送保存定时任务日志的消息*/
    @PostMapping("news/saveCrontabLog")
    ApiResult saveCrontabLog(@RequestBody Object message);

    /*发送打印控制台日志的消息*/
    @PostMapping("news/printConsoleLog")
    ApiResult printConsoleLog(@RequestBody Object message);

    /*发送登录通知的邮件消息*/
    @PostMapping("news/sendLoginInformMail")
    ApiResult sendLoginInformMail(@RequestBody List<String> messageList);

    /*发送短信消息*/
    @PostMapping("news/sendMessage")
    ApiResult sendMessage(@RequestBody Object message);

    /*定时采集股票信息*/
    @GetMapping("stocks/collectData")
    ApiResult collectStocksData();
}