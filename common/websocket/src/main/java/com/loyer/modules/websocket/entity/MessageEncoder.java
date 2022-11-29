package com.loyer.modules.websocket.entity;

import com.alibaba.fastjson.JSON;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * 消息实体类的编码器
 *
 * @author kuangq
 * @date 2021-07-19 17:27
 */
public class MessageEncoder implements Encoder.Text<Message> {

    @Override
    public String encode(Message message) {
        if (message == null) {
            return "";
        }
        return JSON.toJSONString(message);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }
}