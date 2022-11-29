package com.loyer.modules.system.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.Dict;
import com.loyer.modules.system.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 字典Controller
 *
 * @author kuangq
 * @date 2020-05-16 17:02
 */
@Api(tags = "字典模块")
@RestController
@RequestMapping("dict")
public class DictController {

    @Resource
    private DictService dictService;

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAnyPermissions('dict:insert','dict:update')")
    @ApiOperation("保存字典信息")
    @PostMapping("saveDict")
    public ApiResult saveDict(@RequestBody Dict dict) {
        return dictService.saveDict(dict);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('dict:select')")
    @ApiOperation("查询字典信息")
    @PostMapping("selectDict")
    public ApiResult selectDict(@RequestBody Dict dict) {
        return dictService.selectDict(dict);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('dict:delete')")
    @ApiOperation("删除字典信息")
    @PostMapping("deleteDict")
    public ApiResult deleteDict(@RequestBody String[] dictIds) {
        return dictService.deleteDict(dictIds);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('dict:delete')")
    @ApiOperation("字典级联查询")
    @PostMapping("selectCascade")
    public ApiResult selectCascade(@RequestBody String[] uuids) {
        return dictService.selectCascade(uuids);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAnyPermissions('dict:insert','dict:update')")
    @ApiOperation("校验字典信息是否存在")
    @PostMapping("checkDictExists")
    public ApiResult checkDictExists(@RequestBody Dict dict) {
        return dictService.checkDictExists(dict);
    }
}