package com.loyer.common.rabbitmq.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * 秒杀队列的消息监听
 *
 * @author kuangq
 * @date 2020-09-28 14:17
 */
@Component("seckillListener")
public class SeckillListener implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) {

    }
}