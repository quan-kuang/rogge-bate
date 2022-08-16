package com.loyer.common.apis.demote;

import com.loyer.common.apis.server.ToolsServer;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.utils.GeneralUtil;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author kuangq
 * @title ToolsDemote
 * @description 工具服务降级处理
 * @date 2021-03-03 13:43
 */
@Component
public class ToolsDemote implements FallbackFactory<ToolsServer> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ToolsServer create(Throwable throwable) {
        String msg = GeneralUtil.getErrorMessage(throwable);
        Object data = GeneralUtil.getStackTrace(throwable);
        logger.error("TOOLS服务异常：{}", msg);
        return new ToolsServer() {
            @Override
            public ApiResult getSequence() {
                return ApiResult.failure(msg, data);
            }

            @Override
            public ApiResult armToMp3(String amrPath, String mp3Path) {
                return ApiResult.failure(msg, data);
            }

            @Override
            public ApiResult compressVideo(String sourcePath, String targetPath) {
                return ApiResult.failure(msg, data);
            }
        };
    }
}