package com.loyer.common.core.control;

import com.loyer.common.apis.server.MessageServer;
import com.loyer.common.core.entity.ConsoleLog;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.dedicine.utils.ExceptionUtil;
import lombok.SneakyThrows;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 控制器基类
 *
 * @author kuangq
 * @date 2021-05-26 15:41
 */
public class BaseControl {

    private final MessageServer messageServer;

    public BaseControl(MessageServer messageServer) {
        this.messageServer = messageServer;
    }

    /**
     * 获取sessionId，并设置到响应header中
     *
     * @author kuangq
     * @date 2022-01-27 15:58
     */
    @SneakyThrows
    protected String getSessionId() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Assert.notNull(servletRequestAttributes, "can not get request attributes");
        HttpServletRequest httpServletRequest = servletRequestAttributes.getRequest();
        httpServletRequest.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String sessionId = httpServletRequest.getParameter("sessionId");
        Assert.hasLength(sessionId, "can not get session id");
        HttpServletResponse httpServletResponse = servletRequestAttributes.getResponse();
        Assert.notNull(httpServletResponse, "can not http servlet response");
        httpServletResponse.setHeader("sessionId", sessionId);
        return sessionId;
    }

    /**
     * 消费型接口：有输入，无输出
     *
     * @author kuangq
     * @date 2021-05-26 22:29
     */
    protected void invoke(Consumer<String> consumer) {
        String sessionId = getSessionId();
        try {
            consumer.accept(sessionId);
        } catch (Exception e) {
            messageServer.printConsoleLog(new ConsoleLog(sessionId, ExceptionUtil.getErrorMessage(e)));
            throw new BusinessException(e);
        } finally {
            messageServer.printConsoleLog(new ConsoleLog(sessionId));
        }
    }

    /**
     * 供给型接口：无输入，有输出
     *
     * @author kuangq
     * @date 2021-05-26 22:28
     */
    protected <T> T invoke(Supplier<T> supplier) {
        String sessionId = getSessionId();
        return invoke(sessionId, supplier::get);
    }

    /**
     * 函数型接口：有输入，有输出
     *
     * @author kuangq
     * @date 2021-05-26 22:28
     */
    protected <R> R invoke(Function<String, R> function) {
        String sessionId = getSessionId();
        return invoke(sessionId, () -> function.apply(sessionId));
    }

    /**
     * 调用异常发送消息
     *
     * @author kuangq
     * @date 2022-08-18 14:54
     */
    private <T> T invoke(String sessionId, Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            messageServer.printConsoleLog(new ConsoleLog(sessionId, String.format("error: %s", ExceptionUtil.getErrorMessage(e))));
            throw new BusinessException(e);
        } finally {
            messageServer.printConsoleLog(new ConsoleLog(sessionId));
        }
    }
}