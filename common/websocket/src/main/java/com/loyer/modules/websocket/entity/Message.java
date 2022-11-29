package com.loyer.modules.websocket.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消息实体类
 *
 * @author kuangq
 * @date 2021-07-19 17:23
 */
@Data
@NoArgsConstructor
public class Message implements Serializable {

    private String noncestr;

    private String signature;

    private String timestamp;

    private String sessionId;

    private String content;
}