package com.loyer.modules.system.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.core.entity.Menu;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 菜单Controller
 *
 * @author kuangq
 * @date 2020-07-30 13:01
 */
@Api(tags = "菜单模块")
@RestController
@RequestMapping("menu")
public class MenuController {

    @Resource
    private MenuService menuService;

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAnyPermissions('menu:insert','menu:update')")
    @ApiOperation("保存菜单信息")
    @PostMapping("saveMenu")
    public ApiResult saveMenu(@RequestBody Menu menu) {
        return menuService.saveMenu(menu);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('menu:select')")
    @ApiOperation("查询菜单信息")
    @PostMapping("selectMenu")
    public ApiResult selectMenu(@RequestBody Menu menu) {
        return menuService.selectMenu(menu);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('menu:delete')")
    @ApiOperation("删除菜单信息")
    @PostMapping("deleteMenu")
    public ApiResult deleteMenu(@RequestBody String[] uuids) {
        return menuService.deleteMenu(uuids);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('menu:update')")
    @ApiOperation("修改菜单信息")
    @PostMapping("updateMenu")
    public ApiResult updateMenu(@RequestBody Menu menu) {
        return menuService.updateMenu(menu);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('menu:delete')")
    @ApiOperation("菜单级联查询")
    @PostMapping("selectCascade")
    public ApiResult selectCascade(@RequestBody String[] uuids) {
        return menuService.selectCascade(uuids);
    }
}