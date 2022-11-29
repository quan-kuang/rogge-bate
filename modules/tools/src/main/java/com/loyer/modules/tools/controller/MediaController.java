package com.loyer.modules.tools.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.tools.utils.VideoUtil;
import com.loyer.modules.tools.utils.VoiceUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 媒体Controller
 *
 * @author kuangq
 * @date 2021-03-06 10:33
 */
@Api(tags = "媒体工具")
@RestController
@RequestMapping("media")
public class MediaController {

    /**
     * arm文件转mp3
     *
     * @author kuangq
     * @date 2020-07-19 9:29
     */
    @OperateLogAnnotation
    @ApiOperation("arm文件转mp3")
    @PostMapping("armToMp3")
    public ApiResult armToMp3(@RequestParam String amrPath, @RequestParam String mp3Path) {
        return VoiceUtil.armToMp3(amrPath, mp3Path);
    }

    /**
     * 视频压缩
     *
     * @author kuangq
     * @date 2020-11-04 14:53
     */
    @OperateLogAnnotation
    @ApiOperation("视频压缩")
    @PostMapping("compressVideo")
    public ApiResult compressVideo(@RequestParam String sourcePath, @RequestParam String targetPath) {
        return VideoUtil.compressVideo(sourcePath, targetPath);
    }
}