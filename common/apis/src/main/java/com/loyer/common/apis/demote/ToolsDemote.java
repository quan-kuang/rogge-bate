package com.loyer.common.apis.demote;

import com.loyer.common.apis.server.ToolsServer;
import com.loyer.common.dedicine.entity.ApiResult;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 工具服务降级处理
 *
 * @author kuangq
 * @date 2021-03-03 13:43
 */
@Component
public class ToolsDemote implements FallbackFactory<ToolsServer> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ToolsServer create(Throwable throwable) {
        ApiResult apiResult = ApiResult.failure(throwable);
        logger.error("TOOLS服务异常：{}", apiResult.getMsg());
        return new ToolsServer() {
            @Override
            public ApiResult getSequence() {
                return apiResult;
            }

            @Override
            public ApiResult armToMp3(String amrPath, String mp3Path) {
                return apiResult;
            }

            @Override
            public ApiResult compressVideo(String sourcePath, String targetPath) {
                return apiResult;
            }
        };
    }
}