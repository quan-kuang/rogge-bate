package com.loyer.modules.tools.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.core.utils.document.FileUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.security.annotation.ThrottlingAnnotation;
import com.loyer.modules.tools.entity.ExcelData;
import com.loyer.modules.tools.entity.MethodEntity;
import com.loyer.modules.tools.service.UtilService;
import com.loyer.modules.tools.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

/**
 * 工具Controller
 *
 * @author kuangq
 * @date 2019-07-16 15:26
 */
@Api(tags = "工具模块")
@RestController
@RequestMapping("util")
public class UtilController {

    @Resource
    private UtilService utilService;

    @ThrottlingAnnotation(count = 1000)
    @OperateLogAnnotation
    @ApiOperation("获取序列号")
    @GetMapping("getSequence")
    public ApiResult getSequence() {
        return utilService.getSequence();
    }

    @OperateLogAnnotation
    @ApiOperation("获取当前服务器信息")
    @GetMapping("getSreverInfo")
    public ApiResult getSreverInfo() {
        return ServerUtil.getSreverInfo();
    }

    @OperateLogAnnotation
    @ApiOperation("创建PDF")
    @PostMapping("createPdf")
    public ApiResult createPdf(@RequestBody Map<String, Object> params) {
        return PdfUtil.createPdf(params);
    }

    @ApiOperation("导出Excel")
    @PostMapping("exportExcel")
    public void exportExcel(HttpServletResponse httpServletResponse, @Validated @RequestBody ExcelData excelData) {
        byte[] bytes = ExcelUtil.getExcelBytes(excelData);
        FileUtil.download(httpServletResponse, bytes, excelData.getFileName());
    }

    @OperateLogAnnotation
    @ApiOperation("获取指定包下所有类名")
    @GetMapping("getClassName")
    public ApiResult getClassName(@RequestParam String packageName, @RequestParam boolean isRecursion) {
        Set<String> set = ClassUtil.getClassName(packageName, isRecursion);
        return ApiResult.success(set);
    }

    @OperateLogAnnotation
    @ApiOperation("获取指定类下所有方法")
    @GetMapping("getMethod")
    public ApiResult getMethod(@RequestParam String className) {
        Set<MethodEntity> set = MethodUtil.getMethod(className);
        return ApiResult.success(set);
    }

    @OperateLogAnnotation
    @ApiOperation("动态调用方法")
    @PostMapping("invoke")
    public ApiResult invoke(@RequestBody Map<String, Object> params) {
        return MethodUtil.invoke(params);
    }
}