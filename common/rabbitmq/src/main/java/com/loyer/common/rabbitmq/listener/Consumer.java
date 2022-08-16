package com.loyer.common.rabbitmq.listener;

import com.alibaba.fastjson.JSONObject;
import com.loyer.common.apis.server.SystemServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author kuangq
 * @title Consumer
 * @description 消费者队列
 * @date 2020-09-28 11:30
 */
@Component
public class Consumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private SystemServer systemServer;

    /**
     * @param message
     * @return void
     * @author kuangq
     * @description 保存操作日志的队列处理
     * @date 2020-12-17 16:58
     */
    @RabbitListener(queues = "operationLogQueue")
    public void operationLogQueue(@Payload byte[] message) {
        try {
            //接口调用
            systemServer.insertOperateLog(JSONObject.parse(message));
        } catch (Exception e) {
            logger.error("【操作日志保存失败】{}", e.getMessage());
        }
    }

    /**
     * @param message
     * @return void
     * @author kuangq
     * @description 保存定时任务日志的队列处理
     * @date 2020-12-17 16:58
     */
    @RabbitListener(queues = "crontabLogQueue")
    public void crontabLogQueue(@Payload byte[] message) {
        try {
            systemServer.insertCrontabLog(JSONObject.parse(message));
        } catch (Exception e) {
            logger.error("【定时任务日志保存失败】{}", e.getMessage());
        }
    }
}