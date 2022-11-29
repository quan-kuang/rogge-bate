package com.loyer.modules.tools.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.tools.entity.DockerEntity;
import com.loyer.modules.tools.utils.DockerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * docker操作Controller
 *
 * @author kuangq
 * @date 2021-11-18 17:20
 */
@Api(tags = "容器操作模块")
@RestController
@RequestMapping("docker")
public class DockerController {

    @OperateLogAnnotation
    @ApiOperation("启动容器")
    @PostMapping("startContainer")
    public ApiResult startContainer(@Validated @RequestBody DockerEntity dockerEntity) {
        DockerUtil.startContainer(dockerEntity);
        return ApiResult.success();
    }

    @OperateLogAnnotation
    @ApiOperation("停止容器")
    @PostMapping("stopContainer")
    public ApiResult stopContainer(@Validated @RequestBody DockerEntity dockerEntity) {
        DockerUtil.stopContainer(dockerEntity);
        return ApiResult.success();
    }

    @OperateLogAnnotation
    @ApiOperation("重启容器")
    @PostMapping("restartContainer")
    public ApiResult restartContainer(@Validated @RequestBody DockerEntity dockerEntity) {
        DockerUtil.restartContainer(dockerEntity);
        return ApiResult.success();
    }

    @OperateLogAnnotation
    @ApiOperation("删除容器")
    @PostMapping("removeContainer")
    public ApiResult removeContainer(@Validated @RequestBody DockerEntity dockerEntity) {
        DockerUtil.removeContainer(dockerEntity);
        return ApiResult.success();
    }

    @OperateLogAnnotation
    @ApiOperation("执行命令")
    @PostMapping("execCommand")
    public ApiResult execCommand(@Validated @RequestBody DockerEntity dockerEntity) {
        return DockerUtil.execCommand(dockerEntity);
    }
}