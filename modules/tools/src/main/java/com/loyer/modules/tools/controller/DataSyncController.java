package com.loyer.modules.tools.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.tools.entity.RedisDataSync;
import com.loyer.modules.tools.utils.RedisShakeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据同步模块Controller
 *
 * @author kuangq
 * @date 2022-01-27 11:08
 */
@Api(tags = "数据同步")
@RestController
@RequestMapping("dataSync")
public class DataSyncController {

    @SneakyThrows
    @OperateLogAnnotation
    @ApiOperation("创建任务")
    @PostMapping("create")
    public ApiResult create(@RequestBody RedisDataSync redisDataSync) {
        RedisShakeUtil.create(redisDataSync);
        return ApiResult.success();
    }

    @SneakyThrows
    @OperateLogAnnotation
    @ApiOperation("删除任务")
    @PostMapping("delete")
    public ApiResult delete(@RequestBody RedisDataSync redisDataSync) {
        RedisShakeUtil.delete(redisDataSync);
        return ApiResult.success();
    }
}