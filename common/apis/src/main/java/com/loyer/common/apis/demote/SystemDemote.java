package com.loyer.common.apis.demote;

import com.alibaba.fastjson.JSONObject;
import com.loyer.common.apis.server.SystemServer;
import com.loyer.common.dedicine.entity.ApiResult;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 系统服务降级处理
 *
 * @author kuangq
 * @date 2021-03-03 13:43
 */
@Component
public class SystemDemote implements FallbackFactory<SystemServer> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public SystemServer create(Throwable throwable) {
        ApiResult apiResult = ApiResult.failure(throwable);
        logger.error("SYSTEM服务异常：{}", apiResult.getMsg());
        return new SystemServer() {
            @Override
            public ApiResult selectUser(JSONObject user) {
                return apiResult;
            }

            @Override
            public ApiResult insertOperateLog(Object operateLog) {
                return apiResult;
            }

            @Override
            public ApiResult insertCrontabLog(Object crontabLog) {
                return apiResult;
            }

            @Override
            public ApiResult sendWeChatAlarm(Object weChatAlarm) {
                return apiResult;
            }
        };
    }
}