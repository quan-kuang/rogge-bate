package com.loyer.common.datasource.aspect;

import com.loyer.common.datasource.annotation.DataSourceAnnotation;
import com.loyer.common.datasource.enums.DataSourceType;
import com.loyer.common.datasource.handler.DynamicDataSourceHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 数据源动态切换
 *
 * @author kuangq
 * @date 2019-11-20 17:54
 */
@Aspect
@Component
@Order(-1) //保证该AOP在@Transactional之前执行
public class DynamicDataSourceAspect {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 根据注解DataSourceAnnotation编织切入点
     *
     * @author kuangq
     * @date 2019-11-21 12:15
     */
    @Pointcut("@annotation(com.loyer.common.datasource.annotation.DataSourceAnnotation)")
    private void dataSourcePointcut() {
    }

    /**
     * 切入时间点
     *
     * @author kuangq
     * @date 2020-07-17 15:46
     */
    @Around("dataSourcePointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //获取切入点的数据源注解
        DataSourceAnnotation dataSourceAnnotation = getDataSourceAnnotation(proceedingJoinPoint);
        //注解不为空并且设置无需切换默认采用JPA数据源
        if (dataSourceAnnotation != null && !dataSourceAnnotation.isToggle()) {
            logger.info("切换到数据源【JPA】");
        } else {
            //注解为空使用默认数据源
            DataSourceType dataSourceType = dataSourceAnnotation != null ? dataSourceAnnotation.value() : DataSourceType.POSTGRESQL;
            //设置数据源
            DynamicDataSourceHandler.setDataSource(dataSourceType.name());
            //切换到数据源
            logger.info("切换到数据源【{}】", DynamicDataSourceHandler.getDataSource());
        }
        try {
            //目标方法执行
            return proceedingJoinPoint.proceed();
        } finally {
            //方法结束卸载数据源
            DynamicDataSourceHandler.removeDataSource();
        }
    }

    /**
     * 获取切入点的数据源注解
     *
     * @author kuangq
     * @date 2020-07-17 15:34
     */
    private DataSourceAnnotation getDataSourceAnnotation(JoinPoint joinPoint) {
        //获取所在类注解
        DataSourceAnnotation classAnnotation = joinPoint.getTarget().getClass().getAnnotation(DataSourceAnnotation.class);
        //获取所在方法注解
        DataSourceAnnotation methodAnnotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(DataSourceAnnotation.class);
        //方法注解为空选择类注解
        return methodAnnotation != null ? methodAnnotation : classAnnotation;
    }
}