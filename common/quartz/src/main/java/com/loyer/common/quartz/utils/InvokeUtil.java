package com.loyer.common.quartz.utils;

import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kuangq
 * @title InvokeUtil
 * @description 定时任务调用工具类
 * @date 2020-12-16 22:51
 */
public class InvokeUtil {

    /**
     * @param invokeTarget 获取调用目标字符串
     * @param isInvoke     是否调用，是返回方法执行结果，否返回Method对象
     * @return java.lang.Object
     * @author kuangq
     * @description 调用定时任务的目标方法
     * @date 2020-12-18 11:39
     */
    public static Object invokeMethod(String invokeTarget, boolean isInvoke) {
        try {
            //获取调用方法的实例
            Object instance = getInstance(invokeTarget);
            //截取methodName
            String methodName = getMethodName(invokeTarget);
            //调用方法入参的类类型和具体参数
            Map<Class<?>[], Object[]> methodParams = getMethodParams(invokeTarget);
            //动态调用
            return invokeMethod(instance, methodName, methodParams, isInvoke);
        } catch (ClassNotFoundException e) {
            throw new BusinessException(HintEnum.HINT_1032, e);
        } catch (IllegalAccessException e) {
            throw new BusinessException(HintEnum.HINT_1030, e);
        } catch (InstantiationException e) {
            throw new BusinessException(HintEnum.HINT_1036, e);
        } catch (NoSuchMethodException e) {
            throw new BusinessException(HintEnum.HINT_1029, e);
        } catch (InvocationTargetException e) {
            throw new BusinessException(HintEnum.HINT_1031, e.getCause().getMessage());
        } catch (NoSuchBeanDefinitionException e) {
            throw new BusinessException(e.getMessage(), invokeTarget);
        }
    }

    /**
     * @param instance
     * @param methodName
     * @param methodParams
     * @param isInvoke
     * @return java.lang.Object
     * @author kuangq
     * @description 调用目标方法
     * @date 2020-12-18 11:41
     */
    private static Object invokeMethod(Object instance, String methodName, Map<Class<?>[], Object[]> methodParams, boolean isInvoke) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //isInvoke为false只返回方法实例
        Method method;
        if (methodParams.isEmpty()) {
            //无参方法调用
            method = instance.getClass().getDeclaredMethod(methodName);
            if (isInvoke) {
                return method.invoke(instance);
            }
        } else {
            //有参方法调用
            method = instance.getClass().getDeclaredMethod(methodName, methodParams.keySet().iterator().next());
            if (isInvoke) {
                return method.invoke(instance, methodParams.entrySet().iterator().next().getValue());
            }
        }
        return method;
    }

    /**
     * @param invokeTarget
     * @return java.lang.Object
     * @author kuangq
     * @description 获取目标的对象实例
     * @date 2020-12-17 10:40
     */
    private static Object getInstance(String invokeTarget) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchBeanDefinitionException {
        String name = StringUtils.substringBefore(invokeTarget, SpecialCharsConst.LEFT_BRACKET);
        //根据.字符大于1判断是否为className
        if (StringUtils.countMatches(name, SpecialCharsConst.PERIOD) > 1) {
            //根据className获取实例
            name = StringUtils.substringBeforeLast(name, SpecialCharsConst.PERIOD);
            return Class.forName(name).newInstance();
        }
        //根据beanName获取实例
        name = StringUtils.substringBefore(name, SpecialCharsConst.PERIOD);
        return ContextUtil.getBean(name);
    }

    /**
     * @param invokeTarget
     * @return java.lang.String
     * @author kuangq
     * @description 截取方法名称
     * @date 2020-12-17 10:40
     */
    private static String getMethodName(String invokeTarget) {
        String methodName = StringUtils.substringBefore(invokeTarget, SpecialCharsConst.LEFT_BRACKET);
        return StringUtils.substringAfterLast(methodName, SpecialCharsConst.PERIOD);
    }

    /**
     * @param invokeTarget
     * @return java.util.Map<java.lang.Class < ?>[],java.lang.Object[]>
     * @author kuangq
     * @description 获取参数的类类型和具体入参
     * @date 2020-12-17 10:40
     */
    private static Map<Class<?>[], Object[]> getMethodParams(String invokeTarget) {
        //定义返回结果，包含参数类类型和具体参数
        Map<Class<?>[], Object[]> result = new HashMap<>(1);
        //截取调用目标字符串中的入参
        String methodParamStr = StringUtils.substringBetween(invokeTarget, SpecialCharsConst.LEFT_BRACKET, SpecialCharsConst.RIGHT_BRACKET);
        if (StringUtils.isBlank(methodParamStr)) {
            return result;
        }
        //获取调用目标的参数数组
        String[] paramAry = methodParamStr.split(SpecialCharsConst.COMMA);
        int len = paramAry.length;
        //定义返回的参数列表的类类型
        Class<?>[] clazzAry = new Class<?>[len];
        //定义返回的参数
        Object[] uriVariables = new Object[len];
        //遍历目标参数
        for (int i = 0; i < len; i++) {
            String paramStr = StringUtils.trimToEmpty(paramAry[i]);
            //String字符串类型，前后用单引号'包裹
            if (StringUtils.startsWith(paramStr, SpecialCharsConst.SINGLE_QUOTES) && StringUtils.endsWith(paramStr, SpecialCharsConst.SINGLE_QUOTES)) {
                clazzAry[i] = String.class;
                uriVariables[i] = StringUtils.substringBetween(paramStr, SpecialCharsConst.SINGLE_QUOTES);
            }
            //Integer类型，以字符I结尾
            else if (StringUtils.endsWithIgnoreCase(paramStr, "I")) {
                clazzAry[i] = isUpperCase(paramStr) ? Integer.class : int.class;
                uriVariables[i] = Integer.valueOf(StringUtils.removeEndIgnoreCase(paramStr, "I"));
            }
            //Long类型，以字符L结尾
            else if (StringUtils.endsWithIgnoreCase(paramStr, "L")) {
                clazzAry[i] = isUpperCase(paramStr) ? Long.class : long.class;
                uriVariables[i] = Long.valueOf(StringUtils.removeEndIgnoreCase(paramStr, "L"));
            }
            //Float类型，以字符F结尾
            else if (StringUtils.endsWithIgnoreCase(paramStr, "F")) {
                clazzAry[i] = isUpperCase(paramStr) ? Float.class : float.class;
                uriVariables[i] = Float.valueOf(StringUtils.removeEndIgnoreCase(paramStr, "F"));
            }
            //Double类型，以字符D结尾
            else if (StringUtils.endsWithIgnoreCase(paramStr, "D")) {
                clazzAry[i] = isUpperCase(paramStr) ? Double.class : double.class;
                uriVariables[i] = Double.valueOf(StringUtils.removeEndIgnoreCase(paramStr, "D"));
            }
            //Boolean类型，以字符B结尾
            else if (StringUtils.endsWithIgnoreCase(paramStr, "B")) {
                clazzAry[i] = isUpperCase(paramStr) ? Boolean.class : boolean.class;
                uriVariables[i] = Boolean.valueOf(StringUtils.removeEndIgnoreCase(paramStr, "B"));
            }
            //未定义类型抛异常
            else {
                throw new BusinessException(HintEnum.HINT_1080, paramStr);
            }
        }
        result.put(clazzAry, uriVariables);
        return result;
    }

    /**
     * @param paramStr
     * @return boolean
     * @author kuangq
     * @description 判断字符串是否为大小
     * @date 2020-12-17 14:15
     */
    private static boolean isUpperCase(String paramStr) {
        char charAt = paramStr.substring(paramStr.length() - 1).charAt(0);
        return Character.isUpperCase(charAt);
    }
}