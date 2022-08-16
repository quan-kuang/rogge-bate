package com.loyer.common.core.utils.request;

import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.modules.websocket.config.Websocket;

/**
 * @author kuangq
 * @title ConsoleUtil
 * @description 会话日志打印
 * @date 2022-04-02 17:55
 */
public class ConsoleUtil {

    private static final Websocket WEBSOCKET = ContextUtil.getBean(Websocket.class);

    /**
     * @param sessionId
     * @param message
     * @return void
     * @author kuangq
     * @description 单体输出
     * @date 2022-04-06 10:12
     */
    public static void println(String sessionId, String message) {
        WEBSOCKET.unicast(sessionId, message);
    }

    /**
     * @param sessionId
     * @return void
     * @author kuangq
     * @description 关闭会话
     * @date 2022-04-06 10:12
     */
    public static void close(String sessionId) {
        WEBSOCKET.onClose(sessionId);
    }
}