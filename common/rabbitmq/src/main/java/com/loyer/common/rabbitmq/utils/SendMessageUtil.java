package com.loyer.common.rabbitmq.utils;

import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.common.rabbitmq.enums.MessageTemplate;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * MQ消息发送工具类
 *
 * @author kuangq
 * @date 2020-12-10 0:31
 */
public class SendMessageUtil {

    private static final RabbitTemplate RABBIT_TEMPLATE = ContextUtil.getBean(RabbitTemplate.class);

    /**
     * 转换消息发送
     *
     * @author kuangq
     * @date 2020-12-10 9:23
     */
    public static void send(MessageTemplate messageTemplate, Object message) {
        CorrelationData correlationData = new CorrelationData(messageTemplate.getQueue());
        RABBIT_TEMPLATE.convertAndSend(messageTemplate.getExchange(), messageTemplate.getRoutingKey(), message, correlationData);
    }
}