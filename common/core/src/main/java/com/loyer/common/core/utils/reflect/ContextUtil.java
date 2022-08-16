package com.loyer.common.core.utils.reflect;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author kuangq
 * @title ContextUtil
 * @description Spring上下文工具类
 * @date 2019-08-28 17:13
 */
@Component
public class ContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @SuppressWarnings("NullableProblems")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ContextUtil.applicationContext = applicationContext;
    }

    /**
     * @param
     * @return org.springframework.context.ApplicationContext
     * @author kuangq
     * @description 得到Spring上下文环境
     * @date 2019-08-28 17:22
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * @param name
     * @return T
     * @author kuangq
     * @description 通过Spring Bean name 得到Bean
     * @date 2019-08-28 17:22
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * @param clazz
     * @return T
     * @author kuangq
     * @description 通过类型得到Bean
     * @date 2019-08-28 17:22
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}