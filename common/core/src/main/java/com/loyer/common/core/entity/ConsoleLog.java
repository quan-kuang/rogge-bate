package com.loyer.common.core.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 控制台日志
 *
 * @author kuangq
 * @date 2022-08-18 10:51
 */
@Data
@AllArgsConstructor
public class ConsoleLog {

    private String sessionId;

    private String content;

    private Boolean active;

    public ConsoleLog(String sessionId, String content) {
        this.sessionId = sessionId;
        this.content = content;
        this.active = true;
    }

    public ConsoleLog(String sessionId) {
        this.sessionId = sessionId;
        this.active = false;
    }
}