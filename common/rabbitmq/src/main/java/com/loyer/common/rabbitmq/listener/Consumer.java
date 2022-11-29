package com.loyer.common.rabbitmq.listener;

import com.alibaba.fastjson.JSONObject;
import com.loyer.common.apis.server.SystemServer;
import com.loyer.common.core.entity.ConsoleLog;
import com.loyer.common.rabbitmq.utils.ConsoleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 消费者队列
 *
 * @author kuangq
 * @date 2020-09-28 11:30
 */
@Component
public class Consumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private SystemServer systemServer;

    /**
     * 保存操作日志的队列处理
     *
     * @author kuangq
     * @date 2020-12-17 16:58
     */
    @RabbitListener(queues = "OPERATION_LOG_QUEUE")
    public void operationLogQueue(@Payload byte[] message) {
        try {
            systemServer.insertOperateLog(JSONObject.parse(message));
        } catch (Exception e) {
            logger.error("【操作日志保存失败】{}", e.getMessage());
        }
    }

    /**
     * 保存定时任务日志的队列处理
     *
     * @author kuangq
     * @date 2020-12-17 16:58
     */
    @RabbitListener(queues = "CRONTAB_LOG_QUEUE")
    public void crontabLogQueue(@Payload byte[] message) {
        try {
            systemServer.insertCrontabLog(JSONObject.parse(message));
        } catch (Exception e) {
            logger.error("【定时任务日志保存失败】{}", e.getMessage());
        }
    }

    /**
     * 控制台打印
     *
     * @author kuangq
     * @date 2022-08-18 10:37
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(), exchange = @Exchange(name = "CONSOLE_LOG_EXCHANGE", type = ExchangeTypes.FANOUT)))
    public void consoleLogQueue(@Payload byte[] message) {
        try {
            ConsoleUtil.println(JSONObject.parseObject(message, ConsoleLog.class));
        } catch (Exception e) {
            logger.error("【控制台日志打印失败】{}", e.getMessage());
        }
    }
}