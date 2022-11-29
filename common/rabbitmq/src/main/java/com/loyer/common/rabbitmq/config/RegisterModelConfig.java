package com.loyer.common.rabbitmq.config;

import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.rabbitmq.enums.BroadcastMode;
import com.loyer.common.rabbitmq.enums.MessageTemplate;
import org.springframework.amqp.core.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * 注册消息模板（定义队列，交换机，绑定）
 *
 * @author kuangq
 * @date 2020-12-10 0:00
 */
@SuppressWarnings("NullableProblems")
@Configuration
public class RegisterModelConfig implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        //TODO 注册BEAN对象
    }

    /**
     * 动态注册队列，交换机，绑定的bean
     *
     * @author kuangq
     * @date 2020-12-10 9:44
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //强转获取BeanFactory，调用registerBeanDefinition方法注册bean
        DefaultListableBeanFactory defaultListableBeanFactory = ((DefaultListableBeanFactory) configurableListableBeanFactory);
        //遍历消息模板
        Arrays.stream(MessageTemplate.values()).parallel().forEach(messageTemplate -> {
            //获取广播类型
            BroadcastMode broadcastMode = messageTemplate.getBroadcastMode();
            //注册交换机的bean
            String exchangeName = messageTemplate.getExchange();
            BeanDefinition exchangeBean = buildExchange(exchangeName, broadcastMode);
            defaultListableBeanFactory.registerBeanDefinition(exchangeName, exchangeBean);
            //fanout模式无需注册queue和绑定交换机
            if (broadcastMode.equals(BroadcastMode.FANOUT)) {
                return;
            }
            //注册队列的bean
            String queueName = messageTemplate.getQueue();
            BeanDefinition queueBean = buildQueue(queueName);
            defaultListableBeanFactory.registerBeanDefinition(queueName, queueBean);
            //注册绑定队列和交换机的bean
            String routingKeyName = messageTemplate.getRoutingKey();
            BeanDefinition routingKeyBean = buildBinding(routingKeyName, queueName, exchangeName, broadcastMode, defaultListableBeanFactory);
            defaultListableBeanFactory.registerBeanDefinition(routingKeyName, routingKeyBean);
        });
    }

    /**
     * 创建队列的BeanDefinition
     *
     * @author kuangq
     * @date 2020-12-10 0:20
     */
    private BeanDefinition buildQueue(String name) {
        return createBeanDefinition(Queue.class, () -> new Queue(name, true));
    }

    /**
     * 创建交换机的BeanDefinition
     *
     * @author kuangq
     * @date 2022-08-17 17:38
     */
    private BeanDefinition buildExchange(String name, BroadcastMode broadcastMode) {
        switch (broadcastMode) {
            case TOPIC:
                return createBeanDefinition(TopicExchange.class, () -> new TopicExchange(name, true, false));
            case DIRECT:
                return createBeanDefinition(DirectExchange.class, () -> new DirectExchange(name, true, false));
            case FANOUT:
                return createBeanDefinition(FanoutExchange.class, () -> new FanoutExchange(name, true, false));
            default:
                throw new BusinessException(HintEnum.HINT_1080);
        }
    }

    /**
     * 创建绑定队列和交换机的BeanDefinition
     *
     * @author kuangq
     * @date 2022-08-17 17:52
     */
    private BeanDefinition buildBinding(String name, String queueName, String exchangeName, BroadcastMode broadcastMode, DefaultListableBeanFactory defaultListableBeanFactory) {
        Queue queue = defaultListableBeanFactory.getBean(queueName, Queue.class);
        switch (broadcastMode) {
            case TOPIC:
                return createBeanDefinition(Binding.class, () -> BindingBuilder.bind(queue).to(defaultListableBeanFactory.getBean(exchangeName, TopicExchange.class)).with(name));
            case DIRECT:
                return createBeanDefinition(Binding.class, () -> BindingBuilder.bind(queue).to(defaultListableBeanFactory.getBean(exchangeName, DirectExchange.class)).with(name));
            case FANOUT:
                return createBeanDefinition(Binding.class, () -> BindingBuilder.bind(queue).to(defaultListableBeanFactory.getBean(exchangeName, FanoutExchange.class)));
            default:
                throw new BusinessException(HintEnum.HINT_1080);
        }
    }

    /**
     * 创建BeanDefinition
     *
     * @author kuangq
     * @date 2022-08-17 17:03
     */
    private <T> BeanDefinition createBeanDefinition(Class<T> beanClass, Supplier<T> instanceSupplier) {
        return BeanDefinitionBuilder.genericBeanDefinition(beanClass, instanceSupplier).getRawBeanDefinition();
    }
}