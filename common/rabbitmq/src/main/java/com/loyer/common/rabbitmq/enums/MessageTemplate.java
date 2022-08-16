package com.loyer.common.rabbitmq.enums;

import com.loyer.common.rabbitmq.constant.SuffixConst;

/**
 * @author kuangq
 * @title MessageTemplate
 * @description MQ的消息模板（此处设置枚举将自动注册bean）
 * @date 2020-12-10 9:04
 */
public enum MessageTemplate {

    //秒杀消息模板
    SECKILL("seckill"),

    //保存操作日志的消息模板
    OPERATION_LOG("operationLog"),

    //保存定时任务日志的消息模板
    CRONTAB_LOG("crontabLog"),
    ;

    private final String value;

    public String getValue() {
        return value;
    }

    MessageTemplate(String value) {
        this.value = value;
    }

    /**
     * @param
     * @return java.lang.String
     * @author kuangq
     * @description 获取Queue的beanName
     * @date 2020-12-10 9:27
     */
    public String getQueue() {
        return this.value + SuffixConst.QUEUE;
    }

    /**
     * @param
     * @return java.lang.String
     * @author kuangq
     * @description 获取DirectExchange的beanName
     * @date 2020-12-10 9:27
     */
    public String getDirectExchange() {
        return this.value + SuffixConst.DIRECT_EXCHANGE;
    }

    /**
     * @param
     * @return java.lang.String
     * @author kuangq
     * @description 获取RoutingKey的beanName
     * @date 2020-12-10 9:27
     */
    public String getRoutingKey() {
        return this.value + SuffixConst.ROUTING_KEY;
    }
}