package com.loyer.common.core.utils.reflect;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring上下文工具类
 *
 * @author kuangq
 * @date 2019-08-28 17:13
 */
@Component
public class ContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 得到Spring上下文环境
     *
     * @author kuangq
     * @date 2019-08-28 17:22
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ContextUtil.applicationContext = applicationContext;
    }

    /**
     * 通过Spring Bean name 得到Bean
     *
     * @author kuangq
     * @date 2019-08-28 17:22
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 通过类型得到Bean
     *
     * @author kuangq
     * @date 2019-08-28 17:22
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}