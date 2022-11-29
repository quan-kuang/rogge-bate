package com.loyer.modules.system.controller;

import com.loyer.common.core.entity.OperateLog;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.security.annotation.ThrottlingAnnotation;
import com.loyer.modules.system.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 日志Controller
 *
 * @author kuangq
 * @date 2020-06-02 17:11
 */
@Api(tags = "日志模块")
@RestController
@RequestMapping("log")
public class LogController {

    @Resource
    private LogService logService;

    @ThrottlingAnnotation(count = 1000)
    @ApiOperation("保存操作日志")
    @PostMapping("insertOperateLog")
    public ApiResult insertOperateLog(@RequestBody OperateLog operateLog) {
        return ApiResult.success(logService.insertOperateLog(operateLog));
    }

    @ApiOperation("查询操作日志")
    @PostMapping("selectOperateLog")
    public ApiResult selectOperateLog(@RequestBody OperateLog operateLog) {
        return logService.selectOperateLog(operateLog);
    }

    @ApiOperation("ES查询操作日志")
    @PostMapping("selectOperateLogEs")
    public ApiResult selectOperateLogEs(@RequestBody OperateLog operateLog) {
        return logService.selectOperateLogEs(operateLog);
    }

    @ApiOperation("查询操作日志详细出入参")
    @PostMapping("selectOperateArgs")
    public ApiResult selectOperateArgs(@RequestParam String uuid, @RequestParam(defaultValue = "true") boolean isEs) {
        return logService.selectOperateArgs(uuid, isEs);
    }

    @ApiOperation("同步操作日志")
    @PostMapping("syncOperateLog")
    public ApiResult syncOperateLog(@RequestBody OperateLog operateLog) {
        return logService.syncOperateLog(operateLog);
    }

    @ApiOperation("日志周统计")
    @PostMapping("logWeekStat")
    public ApiResult logWeekStat(@RequestBody Map<String, Object> params) {
        return logService.logWeekStat(params);
    }

    @PreAuthorize("@pu.hasAllPermissions('operateLog:delete')")
    @ApiOperation("删除日志信息")
    @PostMapping("deleteOperateLog")
    public ApiResult deleteOperateLog(@RequestBody String[] uuids) {
        return logService.deleteOperateLog(uuids);
    }
}