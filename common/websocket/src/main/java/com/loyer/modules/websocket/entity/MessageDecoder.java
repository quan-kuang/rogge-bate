package com.loyer.modules.websocket.entity;

import com.alibaba.fastjson.JSON;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * 消息实体类的解码器
 *
 * @author kuangq
 * @date 2021-07-19 17:27
 */
public class MessageDecoder implements Decoder.Text<Message> {

    @Override
    public Message decode(String str) {
        try {
            return JSON.parseObject(str, Message.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean willDecode(String str) {
        return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
    }

    @Override
    public void destroy() {
    }
}