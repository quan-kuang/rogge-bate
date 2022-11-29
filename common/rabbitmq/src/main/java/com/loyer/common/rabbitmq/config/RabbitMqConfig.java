package com.loyer.common.rabbitmq.config;

import com.loyer.common.rabbitmq.enums.MessageTemplate;
import com.loyer.common.rabbitmq.listener.SeckillListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * RabbitMq配置文件
 *
 * @author kuangq
 * @date 2020-09-28 10:46
 */
@EnableRabbit
@Configuration
public class RabbitMqConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private CachingConnectionFactory cachingConnectionFactory;

    @Resource
    private Environment environment;

    @Resource
    private SeckillListener seckillListener;

    /**
     * RabbitMQ的使用入口
     *
     * @author kuangq
     * @date 2020-09-28 12:00
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate() {
        //确认机制
        cachingConnectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        //使用单独的发送连接，避免生产者由于各种原因阻塞而导致消费者同样阻塞
        cachingConnectionFactory.setPublisherReturns(true);
        //创建RabbitTemplate一个使用对象
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        //当mandatory设置为true时，如果exchange根据自身类型和消息routingKey无法找到一个合适的queue存储消息，那么broker会调用basic.return方法将消息返还给生产者、为false时，出现上述情况broker会直接将消息丢弃
        rabbitTemplate.setMandatory(true);
        //定义消息转换类型
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        //消息发送成功的回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> logger.info("【消息发送成功】correlationData({})，ack({})，cause({})", Objects.requireNonNull(correlationData).getId(), ack, cause));
        //消息发送失败的回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> logger.error("【消息发送失败】message({})，replyCode({})，replyText({})，exchange({})，routingKey({})", message, replyCode, replyText, exchange, routingKey));
        return rabbitTemplate;
    }

    /**
     * 注册Listener容器
     *
     * @author kuangq
     * @date 2020-10-02 13:55
     */
    @Bean
    public SimpleMessageListenerContainer listenerContainerRobbing() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(cachingConnectionFactory);
        //设置预取计数
        container.setPrefetchCount(Objects.requireNonNull(environment.getProperty("spring.rabbitmq.listener.prefetch", int.class)));
        //并发消费者
        container.setConcurrentConsumers(Objects.requireNonNull(environment.getProperty("spring.rabbitmq.listener.concurrency", int.class)));
        //最大并发用户数
        container.setMaxConcurrentConsumers(Objects.requireNonNull(environment.getProperty("spring.rabbitmq.listener.max-concurrency", int.class)));
        //绑定队列（可设置多个队列）
        container.setQueueNames(MessageTemplate.SECKILL.getQueue());
        //设置消费监听
        container.setMessageListener(seckillListener);
        //消息手动确认、NONE：自动确认、AUTO：根据情况确认
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return container;
    }
}