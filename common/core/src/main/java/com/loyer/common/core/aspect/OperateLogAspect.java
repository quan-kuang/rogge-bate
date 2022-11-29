package com.loyer.common.core.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loyer.common.apis.server.MessageServer;
import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.entity.OperateLog;
import com.loyer.common.core.handler.GlobalExceptionHandler;
import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.core.utils.common.IpUtil;
import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.utils.GeneralUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 保存操作日志的切面
 *
 * @author kuangq
 * @date 2020-05-27 15:01
 */
@Aspect
@Component
public class OperateLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(OperateLogAspect.class);

    //创建线程变量，存储请求开始时间
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    private OperateLogAspect operateLogAspect;

    @Resource
    private MessageServer messageServer;

    /**
     * 根据OperateLogAnnotation注解编织切入点
     *
     * @author kuangq
     * @date 2020-04-02 10:46
     */
    @Pointcut("@annotation(com.loyer.common.core.annotation.OperateLogAnnotation)")
    private void operateLogPointcut() {
    }

    /**
     * 请求执行之前
     *
     * @author kuangq
     * @date 2020-07-17 15:48
     */
    @Before("operateLogPointcut()")
    private void doBefore(JoinPoint joinPoint) {
        START_TIME.set(System.currentTimeMillis());
        logger.info("【请求入参】{}", show(getInArgs(joinPoint)));
    }

    /**
     * 请求结束
     *
     * @author kuangq
     * @date 2020-07-17 15:48
     */
    @AfterReturning(pointcut = "operateLogPointcut()", returning = "object")
    private void doAfterReturning(JoinPoint joinPoint, Object object) {
        logger.info("【响应出参】{}", show(object));
        saveOperateLog(joinPoint, object);
    }

    /**
     * 保存操作日志
     *
     * @author kuangq
     * @date 2020-04-02 11:08
     */
    public OperateLog saveOperateLog(JoinPoint joinPoint, Object object) {
        OperateLog operateLog = new OperateLog();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = Objects.requireNonNull(attributes).getRequest();
        //获取header中的token，跨服务调用携带
        String token = httpServletRequest.getHeader(SystemConst.TOEKN_KEY);
        operateLog.setToken(token);
        //获取请求参数
        String sessionId = httpServletRequest.getParameter("sessionId");
        operateLog.setUuid(StringUtils.defaultIfBlank(sessionId, GeneralUtil.getUuid()));
        operateLog.setServerIp(String.format("%s:%s", httpServletRequest.getLocalAddr(), httpServletRequest.getLocalPort()));
        operateLog.setClientIp(IpUtil.getRealIp(httpServletRequest));
        operateLog.setUrl(httpServletRequest.getRequestURL().toString());
        operateLog.setType(httpServletRequest.getMethod());
        operateLog.setPath(joinPoint.getSignature().getDeclaringTypeName());
        operateLog.setMethod(joinPoint.getSignature().getName());
        operateLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
        //获取所在方法注解
        ApiOperation apiOperation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(ApiOperation.class);
        OperateLogAnnotation operateLogAnnotation = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(OperateLogAnnotation.class);
        //设置标题OperateLogAnnotation未设置value则取值ApiOperation.value()
        if (operateLogAnnotation != null && StringUtils.isNotBlank(operateLogAnnotation.value())) {
            operateLog.setTitle(operateLogAnnotation.value());
        } else if (apiOperation != null && StringUtils.isNotBlank(apiOperation.value())) {
            operateLog.setTitle(apiOperation.value());
        } else {
            operateLog.setTitle("未设置");
        }
        //记录请求时长
        Long startTime = START_TIME.get();
        if (startTime != null) {
            operateLog.setCreateTime(new Timestamp(startTime));
            operateLog.setElapsedTime(DateUtil.getTdoa(startTime));
            START_TIME.remove();
        }
        //获取bean对象
        if (operateLogAspect == null) {
            operateLogAspect = ContextUtil.getBean(OperateLogAspect.class);
        }
        //保存日志
        operateLogAspect.asyncSave(operateLog, getInArgs(joinPoint), object, operateLogAnnotation);
        return operateLog;
    }

    /**
     * rabbitmq异步保存
     *
     * @author kuangq
     * @date 2021-08-06 13:40
     */
    @Async
    public void asyncSave(OperateLog operateLog, String inArgs, Object object, OperateLogAnnotation operateLogAnnotation) {
        //判断是否保存参数（请求异常必须保存）
        if (object instanceof Throwable) {
            operateLog.setInArgs(inArgs);
            operateLog.setOutArgs(getOutArgs(object));
        }
        //非异常情况根据注解保存出入参
        else {
            if (Objects.requireNonNull(operateLogAnnotation).isSaveInArgs()) {
                operateLog.setInArgs(inArgs);
            }
            if (Objects.requireNonNull(operateLogAnnotation).isSaveOutArgs()) {
                operateLog.setOutArgs(getOutArgs(object));
            }
        }
        //设置操作状态
        if (object instanceof Throwable) {
            operateLog.setStatus(false);
        } else if (object instanceof ApiResult) {
            operateLog.setStatus(((ApiResult) object).getFlag());
        } else {
            operateLog.setStatus(true);
        }
        //远程接口保存
        messageServer.saveOperateLog(operateLog);
    }

    /**
     * 获取入参内容
     *
     * @author kuangq
     * @date 2020-07-17 15:47
     */
    private String getInArgs(JoinPoint joinPoint) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest httpServletRequest = Objects.requireNonNull(attributes).getRequest();
            StringBuilder stringBuilder = new StringBuilder();
            //获取url上的参数，主要基于@RequestParam类型
            String queryString = httpServletRequest.getQueryString();
            if (StringUtils.isNotBlank(queryString)) {
                Map<String, String> requestParam = new HashMap<>(8);
                for (String str : queryString.split(SpecialCharsConst.AND)) {
                    String[] queryAry = str.split(SpecialCharsConst.EQUAL_SIGN);
                    requestParam.put(queryAry[0], queryAry[1]);
                }
                stringBuilder.append(JSON.toJSONString(requestParam));
            }
            //判断contentType
            String contentType = httpServletRequest.getContentType();
            if (StringUtils.isBlank(contentType)) {
                return stringBuilder.toString();
            }
            //获取application/json类型参数
            if (contentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                for (Object arg : joinPoint.getArgs()) {
                    if (arg instanceof String || arg instanceof MultipartFile || arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                        continue;
                    }
                    stringBuilder.append(filterParams(arg));
                }
            }
            //获取application/x-www-form-urlencoded类型参数
            else if (contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                Map<String, Object> parameterMap = new HashMap<>(8);
                for (Map.Entry<String, String[]> entry : httpServletRequest.getParameterMap().entrySet()) {
                    String[] values = entry.getValue();
                    parameterMap.put(entry.getKey(), values.length == 1 ? values[0] : values);
                }
                stringBuilder.append(JSON.toJSONString(parameterMap));
            }
            //获取multipart/form-data类型参数
            else if (contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                Map<String, String> multipartFormData = new HashMap<>(8);
                String key, value;
                Enumeration<String> enumeration = httpServletRequest.getParameterNames();
                while (enumeration.hasMoreElements()) {
                    key = enumeration.nextElement();
                    value = httpServletRequest.getParameter(key);
                    multipartFormData.put(key, value);
                }
                stringBuilder.append(JSON.toJSONString(multipartFormData));
            }
            return stringBuilder.toString().trim();
        } catch (Exception e) {
            logger.info("【获取入参失败】{}", e.getMessage());
            return "Failed to get inArgs";
        }
    }

    /**
     * 获取出参内容
     *
     * @author kuangq
     * @date 2021-08-06 13:55
     */
    private String getOutArgs(Object object) {
        return JSON.toJSONString(GlobalExceptionHandler.getResponseResult(object));
    }

    /**
     * 过滤无需保存的参数
     *
     * @author kuangq
     * @date 2020-10-16 10:20
     */
    private String filterParams(Object arg) {
        final String entityKey = "entity";
        if (!arg.getClass().toString().contains(entityKey)) {
            return JSON.toJSONString(arg);
        }
        JSONObject object = JSONObject.parseObject(JSON.toJSONString(arg));
        //删除权限切面设置的params
        final String paramsKey = "params";
        if (object.containsKey(paramsKey)) {
            JSONObject params = object.getJSONObject(paramsKey);
            String[] keys = new String[]{"userId", "userName", "userDeptId", "isAdmin", "inUserIds", "inRoleIds", "permissionScope", "scopeSql"};
            for (String key : keys) {
                params.remove(key);
            }
            if (params.size() > 0) {
                object.put(paramsKey, params);
            } else {
                object.remove(paramsKey);
            }
        }
        //处理登录接口LoginUser对象的多余参数
        final String userKey = "user";
        final String captchaKey = "captcha";
        if (object.containsKey(userKey) && object.containsKey(captchaKey)) {
            String[] keys = new String[]{"username", "password", "enabled", "accountNonLocked", "accountNonExpired", "credentialsNonExpired"};
            for (String key : keys) {
                object.remove(key);
            }
        }
        return JSON.toJSONString(object);
    }

    /**
     * 日志过大打印忽略
     *
     * @author kuangq
     * @date 2020-07-17 16:02
     */
    private Object show(Object object) {
        String result = JSON.toJSONString(object);
        int maxLength = 512;
        if (result.length() > maxLength) {
            return result.substring(0, maxLength).replaceAll("\\\\", "") + "......";
        }
        return object;
    }
}