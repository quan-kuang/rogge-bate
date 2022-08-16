package com.loyer.common.apis.demote;

import com.loyer.common.apis.server.MessageServer;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.utils.GeneralUtil;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author kuangq
 * @title MessageDemote
 * @description 消息服务降级处理
 * @date 2021-03-03 19:19
 */
@Component
public class MessageDemote implements FallbackFactory<MessageServer> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public MessageServer create(Throwable throwable) {
        String msg = GeneralUtil.getErrorMessage(throwable);
        Object data = GeneralUtil.getStackTrace(throwable);
        logger.error("MESSAGE服务异常：{}", msg);
        return new MessageServer() {
            @Override
            public ApiResult saveOperateLog(Object message) {
                return ApiResult.failure(msg, data);
            }

            @Override
            public ApiResult saveCrontabLog(Object message) {
                return ApiResult.failure(msg, data);
            }

            @Override
            public ApiResult sendLoginInformMail(List<String> messageList) {
                return ApiResult.failure(msg, data);
            }

            @Override
            public ApiResult collectStocksData() {
                return ApiResult.failure(msg, data);
            }
        };
    }
}