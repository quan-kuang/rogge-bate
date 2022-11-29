package com.loyer.common.security.aspect;

import com.loyer.common.core.constant.PrefixConst;
import com.loyer.common.core.handler.GlobalExceptionHandler;
import com.loyer.common.core.utils.common.IpUtil;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.redis.utils.CacheUtil;
import com.loyer.common.security.annotation.ThrottlingAnnotation;
import com.loyer.common.security.constant.DefaultConst;
import com.loyer.common.security.enums.ThrottlingType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 接口限流的切面
 *
 * @author kuangq
 * @date 2020-11-05 14:09
 */
@Lazy
@Aspect
@Component
public class ThrottlingAspect {

    private static final String REDIS_SCRIPT = createLuaScript();

    //服务名称
    @Value("${spring.application.name}")
    private String serveName;

    /**
     * 创建lua脚本
     *
     * @author kuangq
     * @date 2020-11-05 15:07
     */
    private static String createLuaScript() {
        return "" +
                "\nredis.replicate_commands()" +
                //不超过最大值，则直接写入时间
                "\nlocal length = redis.call('LLEN', KEYS[1])" +
                "\nif length and tonumber(length) < tonumber(ARGV[1]) then" +
                "\nlocal a = redis.call('TIME')" +
                "\nredis.call('LPUSH', KEYS[1], a[1]*1000000+a[2])" +
                "\nelse" +
                //取出现存的最早的那个时间，和当前时间比较，看是小于时间间隔
                "\nlocal time = redis.call('LINDEX', KEYS[1], -1)" +
                "\nlocal a = redis.call('TIME')" +
                "\nif a[1]*1000000+a[2]-time < tonumber(ARGV[2])*1000000 then" +
                "\nreturn 0" +
                //访问频率超过了限制，返回0表示失败
                "\nelse" +
                "\nredis.call('LPUSH', KEYS[1], a[1]*1000000+a[2])" +
                "\nredis.call('LTRIM', KEYS[1], 0, tonumber(ARGV[1])-1)" +
                "\nend" +
                "\nend" +
                "\nreturn 1";
    }

    /**
     * 获取缓存key值
     *
     * @author kuangq
     * @date 2020-11-05 15:51
     */
    private String getCacheKey(JoinPoint joinPoint, ThrottlingType throttlingType) {
        String key;
        switch (throttlingType) {
            case IP:
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest httpServletRequest = Objects.requireNonNull(attributes).getRequest();
                key = IpUtil.getRealIp(httpServletRequest);
                break;
            case SERVE:
                key = serveName;
                break;
            default:
                throw new BusinessException(HintEnum.HINT_1080, throttlingType);
        }
        String[] clazz = joinPoint.getSignature().getDeclaringTypeName().split("\\.");
        String className = clazz[clazz.length - 1];
        String methodName = joinPoint.getSignature().getName();
        return PrefixConst.THROTTLING + className + ":" + methodName + ":" + key;
    }

    /**
     * 获取切入点的限流注解
     *
     * @author kuangq
     * @date 2020-11-05 17:31
     */
    private ThrottlingAnnotation getThrottlingAnnotation(JoinPoint joinPoint) {
        //获取所在类注解
        ThrottlingAnnotation classAnnotation = joinPoint.getTarget().getClass().getAnnotation(ThrottlingAnnotation.class);
        //获取所在方法注解
        ThrottlingAnnotation methodAnnotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(ThrottlingAnnotation.class);
        //方法注解为空选择类注解
        return methodAnnotation != null ? methodAnnotation : classAnnotation;
    }

    /**
     * 接口限流处理
     *
     * @author kuangq
     * @date 2020-11-05 16:22
     */
    @Around("execution(public * com.loyer.*.*.controller.*.*(..)) || @annotation(com.loyer.common.security.annotation.ThrottlingAnnotation)")
    public Object interceptor(ProceedingJoinPoint joinPoint) {
        //获取限流类型，次数，周期
        int cycle = DefaultConst.THROTTLING_CYCLE;
        int count = DefaultConst.THROTTLING_COUNT;
        ThrottlingType throttlingType = ThrottlingType.SERVE;
        //获取切入点的注解
        ThrottlingAnnotation throttlingAnnotation = getThrottlingAnnotation(joinPoint);
        if (throttlingAnnotation != null) {
            cycle = throttlingAnnotation.cycle();
            count = throttlingAnnotation.count();
            throttlingType = throttlingAnnotation.throttlingType();
        }
        //负数不做限流
        if (cycle > 0 && count > 0) {
            //拼接缓存key值
            String key = getCacheKey(joinPoint, throttlingType);
            //创建redis执行脚本
            Object result = CacheUtil.KEY.execute(Long.class, REDIS_SCRIPT, key, count, cycle);
            //限流判断 0 = FALSE
            //noinspection AlibabaUndefineMagicConstant
            if (result == null || "0".equals(result.toString())) {
                throw new BusinessException(HintEnum.HINT_1089);
            }
        }
        //程序执行
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            //统一响应格式
            return GlobalExceptionHandler.getResponseResult(throwable);
        }
    }
}