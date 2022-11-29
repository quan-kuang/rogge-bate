package com.loyer.modules.system.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.Dept;
import com.loyer.modules.system.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 部门Controller
 *
 * @author kuangq
 * @date 2020-09-10 14:13
 */
@Api(tags = "部门模块")
@RestController
@RequestMapping("dept")
public class DeptController {

    @Resource
    DeptService deptService;

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAnyPermissions('dept:insert','dept:update')")
    @ApiOperation("保存部门信息")
    @PostMapping("saveDept")
    public ApiResult saveDept(@RequestBody Dept dept) {
        return deptService.saveDept(dept);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('dept:select')")
    @ApiOperation("查询部门信息")
    @PostMapping("selectDept")
    public ApiResult selectDept(@RequestBody Dept dept) {
        return deptService.selectDept(dept);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('dept:delete')")
    @ApiOperation("删除部门信息")
    @PostMapping("deleteDept")
    public ApiResult deleteDept(@RequestBody String[] uuids) {
        return deptService.deleteDept(uuids);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('dept:delete')")
    @ApiOperation("部门级联查询")
    @PostMapping("selectCascade")
    public ApiResult selectCascade(@RequestBody String[] uuids) {
        return deptService.selectCascade(uuids);
    }
}