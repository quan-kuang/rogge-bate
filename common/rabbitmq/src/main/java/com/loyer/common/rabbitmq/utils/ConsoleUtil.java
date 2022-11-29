package com.loyer.common.rabbitmq.utils;

import com.loyer.common.core.entity.ConsoleLog;
import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.modules.websocket.config.Websocket;

/**
 * 控制台打印
 *
 * @author kuangq
 * @date 2022-08-18 17:39
 */
public class ConsoleUtil {

    private static final Websocket WEBSOCKET = ContextUtil.getBean(Websocket.class);

    /**
     * 控制台打印
     *
     * @author kuangq
     * @date 2022-04-06 10:12
     */
    public static void println(ConsoleLog consoleLog) {
        if (consoleLog.getActive()) {
            WEBSOCKET.unicast(consoleLog.getSessionId(), consoleLog.getContent());
        } else {
            WEBSOCKET.onClose(consoleLog.getSessionId());
        }
    }
}