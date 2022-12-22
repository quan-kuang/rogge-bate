package com.loyer.modules.tools.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.tools.entity.CacheInfo;
import com.loyer.modules.tools.entity.CacheInfoDetails;
import com.loyer.modules.tools.entity.JedisEntity;
import com.loyer.modules.tools.service.CacheService;
import com.loyer.modules.tools.utils.JedisUtil;
import com.loyer.modules.tools.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    @Resource
    private CacheService cacheService;

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

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('cache:select')")
    @ApiOperation("查询缓存列表")
    @PostMapping("selectCacheInfo")
    public ApiResult selectCacheInfo(@RequestBody CacheInfo cacheInfo) {
        return cacheService.selectCacheInfo(cacheInfo);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('cache:select')")
    @ApiOperation("查询缓存详情")
    @GetMapping("selectCacheInfoDetails")
    public ApiResult selectCacheInfoDetails(@RequestParam String key) {
        return cacheService.selectCacheInfoDetails(key);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('cache:delete')")
    @ApiOperation("删除缓存信息")
    @PostMapping("deleteCacheInfo")
    public ApiResult deleteCacheInfo(@RequestBody CacheInfoDetails cacheInfoDetails) {
        return cacheService.deleteCacheInfo(cacheInfoDetails);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('cache:save')")
    @ApiOperation("保存缓存信息")
    @PostMapping("saveCacheInfo")
    public ApiResult saveCacheInfo(@RequestBody CacheInfoDetails cacheInfoDetails) {
        return cacheService.saveCacheInfo(cacheInfoDetails);
    }
}