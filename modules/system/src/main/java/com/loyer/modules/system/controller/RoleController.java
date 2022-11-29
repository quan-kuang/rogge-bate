package com.loyer.modules.system.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.core.entity.Role;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 角色Controller
 *
 * @author kuangq
 * @date 2020-07-30 12:46
 */
@Api(tags = "角色模块")
@RestController
@RequestMapping("role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAnyPermissions('role:insert','role:update')")
    @ApiOperation("保存角色")
    @PostMapping("saveRole")
    public ApiResult saveRole(@RequestBody Role role) {
        return roleService.saveRole(role);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('role:select')")
    @ApiOperation("查询角色")
    @PostMapping("selectRole")
    public ApiResult selectRole(@RequestBody Role role) {
        return roleService.selectRole(role);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('role:delete')")
    @ApiOperation("删除角色")
    @PostMapping("deleteRole")
    public ApiResult deleteRole(@RequestBody String[] uuids) {
        return roleService.deleteRole(uuids);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('role:update')")
    @ApiOperation("修改角色信息")
    @PostMapping("updateRole")
    public ApiResult updateRole(@RequestBody Role role) {
        return roleService.updateRole(role);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('role:select')")
    @ApiOperation("查询角色关联菜单/部门ID")
    @PostMapping("selectRoleLink")
    public ApiResult selectRoleLink(@RequestBody String[] roleIds) {
        return roleService.selectRoleLink(roleIds);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAnyPermissions('role:insert','role:update')")
    @ApiOperation("校验角色是否存在")
    @GetMapping("checkRoleExists/{name}")
    public ApiResult checkRoleExists(@PathVariable("name") String name) {
        return roleService.checkRoleExists(name);
    }
}