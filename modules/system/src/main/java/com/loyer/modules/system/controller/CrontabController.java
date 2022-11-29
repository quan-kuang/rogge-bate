package com.loyer.modules.system.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.quartz.entity.Crontab;
import com.loyer.common.quartz.entity.CrontabLog;
import com.loyer.modules.system.service.CrontabService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 定时任务Controller
 *
 * @author kuangq
 * @date 2020-12-16 21:58
 */
@Api(tags = "定时任务模块")
@RestController
@RequestMapping("crontab")
public class CrontabController {

    @Resource
    private CrontabService crontabService;

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAnyPermissions('crontab:insert','crontab:update')")
    @ApiOperation("保存定时任务信息")
    @PostMapping("saveCrontab")
    public ApiResult saveCrontab(@RequestBody Crontab crontab) {
        return crontabService.saveCrontab(crontab);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('crontab:select')")
    @ApiOperation("查询定时任务信息")
    @PostMapping("selectCrontab")
    public ApiResult selectCrontab(@RequestBody Crontab crontab) {
        return crontabService.selectCrontab(crontab);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('crontab:select')")
    @ApiOperation("获取表达式执行时间")
    @GetMapping("getExecuteTime")
    public ApiResult getExecuteTime(@RequestParam String expression, @RequestParam(defaultValue = "1") Integer count) {
        return crontabService.getExecuteTime(expression, count);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('crontab:delete')")
    @ApiOperation("删除定时任务信息")
    @PostMapping("deleteCrontab")
    public ApiResult deleteCrontab(@RequestBody String[] uuids) {
        return crontabService.deleteCrontab(uuids);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAnyPermissions('crontab:insert','crontab:update')")
    @ApiOperation("校验corn表达式")
    @GetMapping("checkCornExpression")
    public ApiResult checkCornExpression(@RequestParam String cornExpression) {
        return crontabService.checkCornExpression(cornExpression);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('crontab:execute')")
    @ApiOperation("执行定时任务")
    @PostMapping("executeCrontab")
    public ApiResult executeCrontab(@RequestBody Crontab crontab) {
        return crontabService.executeCrontab(crontab);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('crontab:select','crontabLog:select')")
    @ApiOperation("查询定时任务日志")
    @PostMapping("selectCrontabLog")
    public ApiResult selectCrontabLog(@RequestBody CrontabLog crontabLog) {
        return crontabService.selectCrontabLog(crontabLog);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('crontab:delete','crontabLog:delete')")
    @ApiOperation("删除定时任务日志")
    @PostMapping("deleteCrontabLog")
    public ApiResult deleteCrontabLog(@RequestBody String[] uuids) {
        return crontabService.deleteCrontabLog(uuids);
    }

    @ApiOperation("保存定时任务日志")
    @PostMapping("insertCrontabLog")
    public ApiResult insertCrontabLog(@RequestBody CrontabLog crontabLog) {
        return ApiResult.success(crontabService.insertCrontabLog(crontabLog));
    }
}