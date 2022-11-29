package com.loyer.modules.system.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.Constant;
import com.loyer.modules.system.service.ConstantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 常量信息Controller
 *
 * @author kuangq
 * @date 2021-11-23 14:20
 */
@Api(tags = "常量信息模块")
@RestController
@RequestMapping("constant")
public class ConstantController {

    @Resource
    private ConstantService constantService;

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAnyPermissions('constant:insert','constant:update')")
    @ApiOperation("保存常量信息")
    @PostMapping("saveConstant")
    public ApiResult saveConstant(@RequestBody Constant constant) {
        return constantService.saveConstant(constant);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('constant:select')")
    @ApiOperation("查询常量信息")
    @PostMapping("selectConstant")
    public ApiResult selectConstant(@RequestBody Constant constant) {
        return constantService.selectConstant(constant);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('constant:delete')")
    @ApiOperation("删除常量信息")
    @PostMapping("deleteConstant")
    public ApiResult deleteConstant(@RequestBody Long[] ids) {
        return constantService.deleteConstant(ids);
    }
}