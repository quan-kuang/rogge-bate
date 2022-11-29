package com.loyer.common.dedicine.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 异常处理工具
 *
 * @author kuangq
 * @date 2022-08-23 14:22
 */
public class ExceptionUtil {

    /**
     * 获取错误信息
     *
     * @author kuangq
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
     * 递归异常详情信息
     *
     * @author kuangq
     * @date 2022-07-29 17:52
     */
    private static String getCause(Throwable throwable, StringBuilder errorMessage) {
        String message = throwable.getMessage();
        if (message != null && !errorMessage.toString().contains(message)) {
            errorMessage.append(message);
            errorMessage.append("/");
        }
        if (throwable.getCause() != null) {
            return getCause(throwable.getCause(), errorMessage);
        }
        return errorMessage.deleteCharAt(errorMessage.lastIndexOf("/")).toString();
    }

    /**
     * 获取错误详情
     *
     * @author kuangq
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
     * 递归异常详情信息
     *
     * @author kuangq
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
     * 获取指定行数的异常信息
     *
     * @author kuangq
     * @date 2021-09-10 14:31
     */
    private static List<StackTraceElement> getStackTrace(StackTraceElement[] stackTraceElements, int lineNumber) {
        List<StackTraceElement> stackTraceElementList = Arrays.asList(stackTraceElements);
        if (stackTraceElementList.size() < lineNumber) {
            return stackTraceElementList;
        }
        return stackTraceElementList.subList(0, lineNumber);
    }
}