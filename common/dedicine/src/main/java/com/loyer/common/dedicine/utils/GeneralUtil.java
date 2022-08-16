package com.loyer.common.dedicine.utils;

import com.loyer.common.dedicine.constant.SystemConst;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * @author kuangq
 * @title GeneralUtil
 * @description 通用工具类
 * @date 2019-09-04 21:36
 */
public class GeneralUtil {

    /**
     * @param
     * @return java.lang.String
     * @author kuangq
     * @description 获取UUID
     * @date 2019-09-04 21:58
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * @param throwable
     * @return java.lang.String
     * @author kuangq
     * @description 获取错误信息
     * @date 2021-09-10 13:50
     */
    public static String getErrorMessage(Throwable throwable) {
        try {
            return getCause(throwable, new StringBuilder());
        } catch (Exception ignored) {
        }
        return throwable.getMessage();
    }

    /**
     * @param throwable
     * @param errorMessage
     * @return java.lang.String
     * @author kuangq
     * @description 递归异常详情信息
     * @date 2022-07-29 17:52
     */
    private static String getCause(Throwable throwable, StringBuilder errorMessage) {
        errorMessage.append(throwable.getMessage());
        errorMessage.append("/");
        if (throwable.getCause() != null) {
            return getCause(throwable.getCause(), errorMessage);
        }
        return errorMessage.deleteCharAt(errorMessage.lastIndexOf("/")).toString();
    }

    /**
     * @param throwable
     * @return java.lang.Object
     * @author kuangq
     * @description 获取错误详情
     * @date 2019-11-13 16:08
     */
    public static Object getStackTrace(Throwable throwable) {
        List<StackTraceElement> stackTraceElementList = new LinkedList<>();
        try {
            return getCause(throwable.getCause(), stackTraceElementList);
        } catch (Exception ignored) {
        }
        return stackTraceElementList;
    }

    /**
     * @param throwable
     * @param stackTraceElementList
     * @return java.util.List<java.lang.StackTraceElement>
     * @author kuangq
     * @description 递归异常详情信息
     * @date 2022-07-29 17:57
     */
    private static List<StackTraceElement> getCause(Throwable throwable, List<StackTraceElement> stackTraceElementList) {
        int lineNumber = 3;
        stackTraceElementList.addAll(getStackTrace(throwable.getStackTrace(), lineNumber));
        if (throwable.getCause() != null) {
            return getCause(throwable.getCause(), stackTraceElementList);
        }
        return stackTraceElementList;
    }

    /**
     * @param stackTraceElements
     * @param lineNumber
     * @return java.util.List<java.lang.StackTraceElement>
     * @author kuangq
     * @description 获取指定行数的异常信息
     * @date 2021-09-10 14:31
     */
    private static List<StackTraceElement> getStackTrace(StackTraceElement[] stackTraceElements, int lineNumber) {
        List<StackTraceElement> stackTraceElementList = Arrays.asList(stackTraceElements);
        if (stackTraceElementList.size() < lineNumber) {
            return stackTraceElementList;
        }
        return stackTraceElementList.subList(0, lineNumber);
    }

    /**
     * @param
     * @return java.lang.String
     * @author kuangq
     * @description 获取当前操作系统
     * @date 2020-07-01 23:45
     */
    public static String getOs() {
        String operateSystem = System.getProperty("os.name").toLowerCase();
        if (operateSystem.contains(SystemConst.WINDOWS)) {
            return SystemConst.WINDOWS;
        } else if (operateSystem.contains(SystemConst.LINUX)) {
            return SystemConst.LINUX;
        } else if (operateSystem.contains(SystemConst.MAC)) {
            return SystemConst.MAC;
        } else {
            return "other";
        }
    }

    /**
     * @param millis
     * @return void
     * @author kuangq
     * @description 线程等待
     * @date 2021-07-13 17:49
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }
}