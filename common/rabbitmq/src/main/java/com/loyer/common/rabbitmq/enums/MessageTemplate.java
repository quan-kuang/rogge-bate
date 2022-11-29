package com.loyer.common.rabbitmq.enums;

import com.loyer.common.rabbitmq.constant.SuffixConst;

/**
 * MQ的消息模板（此处设置枚举将自动注册bean）
 *
 * @author kuangq
 * @date 2020-12-10 9:04
 */
public enum MessageTemplate {

    //秒杀消息模板
    SECKILL(BroadcastMode.DIRECT),

    //保存操作日志的消息模板
    OPERATION_LOG(BroadcastMode.DIRECT),

    //保存定时任务日志的消息模板
    CRONTAB_LOG(BroadcastMode.DIRECT),

    //发送控制台日志
    CONSOLE_LOG(BroadcastMode.FANOUT),
    ;

    private final BroadcastMode broadcastMode;

    MessageTemplate(BroadcastMode broadcastMode) {
        this.broadcastMode = broadcastMode;
    }

    public BroadcastMode getBroadcastMode() {
        return broadcastMode;
    }

    /**
     * 获取Queue的beanName
     *
     * @author kuangq
     * @date 2020-12-10 9:27
     */
    public String getQueue() {
        return this.name() + SuffixConst.QUEUE;
    }

    /**
     * 获取Exchange的beanName
     *
     * @author kuangq
     * @date 2020-12-10 9:27
     */
    public String getExchange() {
        return this.name() + SuffixConst.EXCHANGE;
    }

    /**
     * 获取RoutingKey的beanName
     *
     * @author kuangq
     * @date 2020-12-10 9:27
     */
    public String getRoutingKey() {
        return this.name() + SuffixConst.ROUTING_KEY;
    }
}