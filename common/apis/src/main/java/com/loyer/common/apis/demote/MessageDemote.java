package com.loyer.common.apis.demote;

import com.loyer.common.apis.server.MessageServer;
import com.loyer.common.dedicine.entity.ApiResult;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 消息服务降级处理
 *
 * @author kuangq
 * @date 2021-03-03 19:19
 */
@Component
public class MessageDemote implements FallbackFactory<MessageServer> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public MessageServer create(Throwable throwable) {
        ApiResult apiResult = ApiResult.failure(throwable);
        logger.error("MESSAGE服务异常：{}", apiResult.getMsg());
        return new MessageServer() {
            @Override
            public ApiResult saveOperateLog(Object message) {
                return apiResult;
            }

            @Override
            public ApiResult saveCrontabLog(Object message) {
                return apiResult;
            }

            @Override
            public ApiResult printConsoleLog(Object message) {
                return apiResult;
            }

            @Override
            public ApiResult sendLoginInformMail(List<String> messageList) {
                return apiResult;
            }

            @Override
            public ApiResult sendMessage(Object message) {
                return apiResult;
            }

            @Override
            public ApiResult collectStocksData() {
                return apiResult;
            }
        };
    }
}