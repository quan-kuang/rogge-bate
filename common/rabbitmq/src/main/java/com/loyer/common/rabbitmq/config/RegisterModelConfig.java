package com.loyer.common.rabbitmq.config;

import com.loyer.common.rabbitmq.enums.MessageTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Configuration;

/**
 * @author kuangq
 * @title RegisterModelConfig
 * @description 注册消息模板（定义队列，交换机，绑定）
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
     * @param configurableListableBeanFactory
     * @return void
     * @author kuangq
     * @description 动态注册队列，交换机，绑定的bean
     * @date 2020-12-10 9:44
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        //强转获取BeanFactory，调用registerBeanDefinition方法注册bean
        DefaultListableBeanFactory defaultListableBeanFactory = ((DefaultListableBeanFactory) configurableListableBeanFactory);
        //遍历消息模板
        for (MessageTemplate messageTemplate : MessageTemplate.values()) {
            //注册队列的bean对象
            String queueName = messageTemplate.getQueue();
            BeanDefinition queueBean = buildQueue(queueName);
            defaultListableBeanFactory.registerBeanDefinition(queueName, queueBean);
            //注册交换机的bean对象
            String directExchangeName = messageTemplate.getDirectExchange();
            BeanDefinition directExchangeBean = buildDirectExchange(directExchangeName);
            defaultListableBeanFactory.registerBeanDefinition(directExchangeName, directExchangeBean);
            //获取队列和交换机的实例
            Queue queue = defaultListableBeanFactory.getBean(queueName, Queue.class);
            DirectExchange directExchange = defaultListableBeanFactory.getBean(directExchangeName, DirectExchange.class);
            //注册绑定队列和交换机的bean对象
            String routingKeyName = messageTemplate.getRoutingKey();
            BeanDefinition routingKeyBean = buildBinding(routingKeyName, queue, directExchange);
            defaultListableBeanFactory.registerBeanDefinition(routingKeyName, routingKeyBean);
        }
    }

    /**
     * @param name
     * @return org.springframework.beans.factory.config.BeanDefinition
     * @author kuangq
     * @description 创建队列的BeanDefinition
     * @date 2020-12-10 0:20
     */
    private BeanDefinition buildQueue(String name) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(Queue.class,
                () -> new Queue(name, true));
        return beanDefinitionBuilder.getRawBeanDefinition();
    }

    /**
     * @param name
     * @return org.springframework.beans.factory.config.BeanDefinition
     * @author kuangq
     * @description 创建交换机的BeanDefinition
     * @date 2020-12-10 0:20
     */
    private BeanDefinition buildDirectExchange(String name) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DirectExchange.class,
                () -> new DirectExchange(name, true, false));
        return beanDefinitionBuilder.getRawBeanDefinition();
    }

    /**
     * @param name
     * @param queue
     * @param directExchange
     * @return org.springframework.beans.factory.config.BeanDefinition
     * @author kuangq
     * @description 创建绑定队列和交换机的BeanDefinition
     * @date 2020-12-10 0:21
     */
    private BeanDefinition buildBinding(String name, Queue queue, DirectExchange directExchange) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(Binding.class,
                () -> BindingBuilder.bind(queue).to(directExchange).with(name));
        return beanDefinitionBuilder.getRawBeanDefinition();
    }
}