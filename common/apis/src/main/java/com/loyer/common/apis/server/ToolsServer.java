package com.loyer.common.apis.server;

import com.loyer.common.apis.demote.ToolsDemote;
import com.loyer.common.dedicine.entity.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 工具服务
 *
 * @author kuangq
 * @date 2021-03-03 13:42
 */
@FeignClient(name = "TOOLS", fallbackFactory = ToolsDemote.class)
public interface ToolsServer {

    @GetMapping("util/getSequence")
    ApiResult getSequence();

    @PostMapping("media/armToMp3")
    ApiResult armToMp3(@RequestParam String amrPath, @RequestParam String mp3Path);

    @PostMapping("media/compressVideo")
    ApiResult compressVideo(@RequestParam String sourcePath, @RequestParam String targetPath);
}