package com.loyer.modules.tools.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.tools.entity.JedisEntity;
import com.loyer.modules.tools.utils.JedisUtil;
import com.loyer.modules.tools.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 缓存模块Controller
 *
 * @author kuangq
 * @date 2021-09-09 16:01
 */
@Api(tags = "缓存模块")
@RestController
@RequestMapping("cache")
public class CacheController {

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('redis:get')")
    @ApiOperation("获取当前Redis信息")
    @GetMapping("getRedis/{type}")
    public ApiResult getRedis(@PathVariable String type, @RequestParam(required = false) String param) {
        return RedisUtil.getRedis(type, param);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('redis:set')")
    @ApiOperation("设置当前Redis配置")
    @PostMapping("setRedis")
    public ApiResult setRedis(@RequestBody Map<String, String> config) {
        return RedisUtil.configSet(config);
    }

    @OperateLogAnnotation
    @ApiOperation("获取Jedis信息")
    @PreAuthorize("@pu.hasAllPermissions('jedis:get')")
    @PostMapping("getJedis/{type}")
    public ApiResult getJedis(@PathVariable String type, @RequestParam(required = false) String param, @Validated @RequestBody JedisEntity jedisEntity) {
        return JedisUtil.getJedis(jedisEntity, type, param);
    }

    @OperateLogAnnotation
    @ApiOperation("设置Jedis配置")
    @PreAuthorize("@pu.hasAllPermissions('jedis:set')")
    @PostMapping("setJedis")
    public ApiResult setJedis(@Validated @RequestBody JedisEntity jedisEntity) {
        return JedisUtil.configSet(jedisEntity);
    }

    @OperateLogAnnotation
    @ApiOperation("批量写入数据")
    @PreAuthorize("@pu.hasAnyPermissions('redis:set','jedis:set')")
    @PostMapping("batchWriteData")
    public ApiResult batchWriteData(@Validated @RequestBody JedisEntity jedisEntity) {
        return JedisUtil.batchWriteData(jedisEntity);
    }
}