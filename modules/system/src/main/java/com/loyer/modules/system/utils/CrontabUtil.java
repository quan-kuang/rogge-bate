package com.loyer.modules.system.utils;

import com.loyer.common.apis.server.MessageServer;
import com.loyer.common.dedicine.entity.ApiResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时任务工具类
 *
 * @author kuangq
 * @date 2021-09-17 9:46
 */
@Component
public class CrontabUtil {

    @Resource
    private MessageServer messageServer;

    /**
     * 定时采集股票信息
     *
     * @author kuangq
     * @date 2021-09-17 18:33
     */
    public ApiResult collectStocksData() {
        return messageServer.collectStocksData();
    }
}