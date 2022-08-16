package com.loyer.common.rabbitmq.utils;

import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.common.rabbitmq.enums.MessageTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author kuangq
 * @title SendMessageUtil
 * @description MQ消息发送工具类
 * @date 2020-12-10 0:31
 */
public class SendMessageUtil {

    private static final RabbitTemplate RABBIT_TEMPLATE = ContextUtil.getBean(RabbitTemplate.class);

    /**
     * @param messageTemplate
     * @param message
     * @return void
     * @author kuangq
     * @description 转换消息发送
     * @date 2020-12-10 9:23
     */
    public static void send(MessageTemplate messageTemplate, Object message) {
        CorrelationData correlationData = new CorrelationData(messageTemplate.getValue());
        RABBIT_TEMPLATE.convertAndSend(messageTemplate.getDirectExchange(), messageTemplate.getRoutingKey(), message, correlationData);
    }
}