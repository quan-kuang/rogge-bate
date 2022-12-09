package com.loyer.common.core.aspect;

import com.loyer.common.core.entity.OperateLog;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.dedicine.utils.ExceptionUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 统一异常日志保存
 *
 * @author kuangq
 * @date 2019-07-19 10:22
 */
@Aspect
@Component
public class ExceptionLogAspect {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private OperateLogAspect operateLogAspect;

    /**
     * 编织切入点
     *
     * @author kuangq
     * @date 2020-07-17 15:50
     */
    @Pointcut("execution(public * com.loyer.*.*.controller.*.*(..))")
    private void controllerPointcut() {
    }

    /**
     * 异常捕捉处理
     *
     * @author kuangq
     * @date 2020-07-17 15:48
     */
    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "throwable")
    private void exceptionDispose(JoinPoint joinPoint, Throwable throwable) {
        OperateLog operateLog = operateLogAspect.saveOperateLog(joinPoint, throwable);
        if (throwable instanceof BusinessException) {
            logger.error("【业务异常】{}：{}", operateLog.getTitle(), throwable.getMessage());
        } else {
            logger.error("【系统异常】{}：{}", operateLog.getTitle(), ExceptionUtil.getErrorMessage(throwable));
            throwable.printStackTrace();
        }
    }
}