package com.loyer.modules.websocket.config;

import com.alibaba.fastjson.JSON;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.utils.DesUtil;
import com.loyer.common.dedicine.utils.Md5Util;
import com.loyer.modules.websocket.entity.Message;
import com.loyer.modules.websocket.entity.MessageDecoder;
import com.loyer.modules.websocket.entity.MessageEncoder;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Websocket通讯 同步异步说明参考：http://blog.csdn.net/who_is_xiaoming/article/details/53287691
 *
 * @author kuangq
 * @date 2021-07-18 1:34
 */
@ServerEndpoint(value = "/websocket/{noncestr}/{signature}/{timestamp}/{sessionId}", decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
@Component
public class Websocket {

    // 用来存放每个客户端对应的Websocket对象
    private static final ConcurrentHashMap<String, Websocket> WEBSOCKET_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();
    //前端结束标示，据此关闭控制台
    private static final String FINISHED = "finished";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功时调用
     *
     * @author kuangq
     * @date 2021-07-19 22:42
     */
    @SneakyThrows
    @OnOpen
    public void onOpen(Session session) {
        //获取入参
        Map<String, String> params = session.getPathParameters();
        Message message = JSON.parseObject(JSON.toJSONString(params), Message.class);
        String sessionId = message.getSessionId();
        String result = connectAuth(message);
        //鉴权失败
        if (result != null) {
            syncSend(session, result);
            syncSend(session, FINISHED);
            session.close();
            return;
        }
        //加入连接
        this.session = session;
        WEBSOCKET_CONCURRENT_HASH_MAP.put(sessionId, this);
        logger.info("有新的会话加入【{}】，当前总会话数：{}", sessionId, WEBSOCKET_CONCURRENT_HASH_MAP.size());
    }

    /**
     * 连接关闭时调用
     *
     * @author kuangq
     * @date 2021-07-19 22:43
     */
    @OnClose
    public void onClose(@PathParam("sessionId") String sessionId) {
        if (WEBSOCKET_CONCURRENT_HASH_MAP.containsKey(sessionId)) {
            syncSend(WEBSOCKET_CONCURRENT_HASH_MAP.get(sessionId).session, FINISHED);
            WEBSOCKET_CONCURRENT_HASH_MAP.remove(sessionId);
            logger.info("有一会话关闭【{}】，当前总会话数：{}", sessionId, WEBSOCKET_CONCURRENT_HASH_MAP.size());
        }
    }

    /**
     * 发生错误时调用
     *
     * @author kuangq
     * @date 2021-07-19 22:43
     */
    @OnError
    public void onError(@PathParam("sessionId") String sessionId, Session session, Throwable throwable) {
        logger.error("连接发生错误【{}】：{}", sessionId, throwable.getMessage());
        syncSend(session, String.format("error：%s", throwable.getMessage()));
        syncSend(session, FINISHED);
        WEBSOCKET_CONCURRENT_HASH_MAP.remove(sessionId);
    }

    /**
     * 收到客户端消息时调用
     *
     * @author kuangq
     * @date 2021-07-19 22:43
     */
    @OnMessage
    public void onMessage(@PathParam("sessionId") String sessionId, Message message) {
        if (WEBSOCKET_CONCURRENT_HASH_MAP.containsKey(sessionId)) {
            String content = String.format("群发消息【%s】：%s", sessionId, message.getContent());
            logger.info(content);
            broadcast(content);
        }
    }

    /**
     * 指定会话发送消息（同步发送）
     *
     * @author kuangq
     * @date 2021-07-17 23:21
     */
    public boolean unicast(String sessionId, String content) {
        Websocket websocket = WEBSOCKET_CONCURRENT_HASH_MAP.get(sessionId);
        if (websocket == null) {
            return false;
        }
        syncSend(websocket.session, content);
        return true;
    }

    /**
     * 群发消息（异步发送）
     *
     * @author kuangq
     * @date 2019-12-27 18:22
     */
    public void broadcast(String content) {
        if (StringUtils.isBlank(content)) {
            return;
        }
        for (Map.Entry<String, Websocket> websocketEntry : WEBSOCKET_CONCURRENT_HASH_MAP.entrySet()) {
            Session session = websocketEntry.getValue().session;
            if (session == null || !session.isOpen()) {
                continue;
            }
            session.getAsyncRemote().sendText(content);
        }
    }

    /**
     * 同步消息发送
     *
     * @author kuangq
     * @date 2021-07-19 18:29
     */
    private void syncSend(Session session, String content) {
        try {
            if (session == null || !session.isOpen() || StringUtils.isBlank(content)) {
                return;
            }
            session.getBasicRemote().sendText(content);
        } catch (IOException e) {
            logger.error("同步消息发送失败：{}", e.getMessage());
        }
    }

    /**
     * 连接鉴权
     *
     * @author kuangq
     * @date 2021-07-19 22:01
     */
    private String connectAuth(Message message) {
        //验签必传校验
        String noncestr = message.getNoncestr();
        String signature = message.getSignature();
        String timestamp = message.getTimestamp();
        if (StringUtils.isBlank(noncestr) || StringUtils.isBlank(signature) || StringUtils.isBlank(timestamp)) {
            return HintEnum.HINT_1002.getMsg();
        }
        //时间戳校验
        long currentTime = System.currentTimeMillis();
        long times = Long.parseLong(timestamp);
        if (Math.abs(currentTime - times) > SystemConst.SIGN_EXPIRE_TIME) {
            return HintEnum.HINT_1003.getMsg();
        }
        //签名校验
        String desEncryptStr = DesUtil.encrypt(noncestr + "&" + timestamp);
        String md5EncryptStr = Md5Util.encrypt(desEncryptStr);
        if (!signature.equals(md5EncryptStr)) {
            return HintEnum.HINT_1004.getMsg();
        }
        //添加认证
        return null;
    }
}