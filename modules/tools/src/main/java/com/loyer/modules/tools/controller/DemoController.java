package com.loyer.modules.tools.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.security.annotation.ThrottlingAnnotation;
import com.loyer.common.security.enums.ThrottlingType;
import com.loyer.modules.tools.service.DemoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 示例Controller
 *
 * @author kuangq
 * @date 2019-07-16 15:26
 */
@Api(tags = "示例模块")
@RestController
@RequestMapping("demo")
public class DemoController {

    @Resource
    private DemoService demoService;

    @ApiIgnore
    @ThrottlingAnnotation(cycle = 10, throttlingType = ThrottlingType.IP)
    @OperateLogAnnotation
    @ApiOperation("查询版本号")
    @GetMapping("showVersion")
    public ApiResult showVersion() {
        return ApiResult.success(SystemConst.VERSION);
    }

    @OperateLogAnnotation
    @ApiOperation("获取缓存数据")
    @GetMapping("getValue/{key}")
    public ApiResult getValue(@PathVariable("key") String key) {
        return demoService.getValue(key);
    }

    @OperateLogAnnotation
    @ApiOperation("保存数据到redis")
    @PostMapping("setValue")
    public ApiResult setValue(@RequestBody Map<String, Object> params) {
        return demoService.setValue(params);
    }
}