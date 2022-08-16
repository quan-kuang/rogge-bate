package com.loyer.common.core.control;

import com.loyer.common.core.utils.request.ConsoleUtil;
import lombok.SneakyThrows;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author kuangq
 * @title BaseControl
 * @description 控制器基类
 * @date 2021-05-26 15:41
 */
public class BaseControl {

    private final String SESSION_ID = "sessionId";

    /**
     * @param
     * @return javax.servlet.http.HttpServletRequest
     * @author kuangq
     * @description 获取HttpServletRequest对象
     * @date 2022-01-27 15:57
     */
    protected HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }

    /**
     * @param
     * @return javax.servlet.http.HttpServletResponse
     * @author kuangq
     * @description 获取HttpServletResponse对象
     * @date 2022-04-06 10:15
     */
    protected HttpServletResponse getHttpServletResponse() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
    }

    /**
     * @param
     * @return java.lang.String
     * @author kuangq
     * @description 获取sessionId，用于websocket
     * @date 2022-01-27 15:58
     */
    @SneakyThrows
    protected String getSessionId() {
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        httpServletRequest.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return httpServletRequest.getParameter(SESSION_ID);
    }

    /**
     * @param httpServletResponse
     * @return java.lang.String
     * @author kuangq
     * @description 获取sessionId，并设置到响应header中
     * @date 2022-04-06 10:02
     */
    protected String getSessionId(HttpServletResponse httpServletResponse) {
        String sessionId = getSessionId();
        httpServletResponse.setHeader(SESSION_ID, sessionId);
        return sessionId;
    }

    /**
     * @param consumer
     * @return void
     * @author kuangq
     * @description 消费型接口：有输入，无输出
     * @date 2021-05-26 22:29
     */
    protected void invoke(Consumer<String> consumer) {
        HttpServletResponse httpServletResponse = getHttpServletResponse();
        String sessionId = getSessionId(httpServletResponse);
        try {
            consumer.accept(sessionId);
        } finally {
            ConsoleUtil.close(sessionId);
        }
    }

    /**
     * @param supplier
     * @return T
     * @author kuangq
     * @description 供给型接口：无输入，有输出
     * @date 2021-05-26 22:28
     */
    protected <T> T invoke(Supplier<T> supplier) {
        HttpServletResponse httpServletResponse = getHttpServletResponse();
        String sessionId = getSessionId(httpServletResponse);
        try {
            return supplier.get();
        } finally {
            ConsoleUtil.close(sessionId);
        }
    }

    /**
     * @param function
     * @return R
     * @author kuangq
     * @description 函数型接口：有输入，有输出
     * @date 2021-05-26 22:28
     */
    protected <R> R invoke(Function<String, R> function) {
        HttpServletResponse httpServletResponse = getHttpServletResponse();
        String sessionId = getSessionId(httpServletResponse);
        try {
            return function.apply(sessionId);
        } finally {
            ConsoleUtil.close(sessionId);
        }
    }
}