package com.loyer.modules.system.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.OnLineUser;
import com.loyer.modules.system.service.OnLineUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 在线用户Controller
 *
 * @author kuangq
 * @date 2020-12-15 17:44
 */
@Api(tags = "在线用户模块")
@RestController
@RequestMapping("onLineUser")
public class OnLineUserController {

    @Resource
    private OnLineUserService onLineUserService;

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('onLineUser:select')")
    @ApiOperation("查询在线用户")
    @PostMapping("selectOnLineUser")
    public ApiResult selectOnLineUser(@RequestBody OnLineUser onLineUser) {
        return onLineUserService.selectOnLineUser(onLineUser);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('onLineUser:delete')")
    @ApiOperation("删除在线用户")
    @PostMapping("deleteOnLineUser")
    public ApiResult deleteOnLineUser(@RequestBody String[] uuids) {
        return onLineUserService.deleteOnLineUser(uuids);
    }
}