package com.loyer.modules.system.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.Attachment;
import com.loyer.modules.system.service.AttachmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 附件Controller
 *
 * @author kuangq
 * @date 2020-08-02 15:20
 */
@Api(tags = "附件模块")
@RestController
@RequestMapping("attachment")
public class AttachmentController {

    @Resource
    private AttachmentService attachmentService;

    @SuppressWarnings("unused")
    @OperateLogAnnotation
    @ApiOperation("附件上传")
    @PostMapping("upload")
    public ApiResult upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile multipartFile) {
        return attachmentService.upload(httpServletRequest);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAnyPermissions('attachment:insert','attachment:update')")
    @ApiOperation("保存附件信息")
    @PostMapping("saveAttachment")
    public ApiResult saveAttachment(@RequestBody Attachment attachment) {
        return attachmentService.saveAttachment(attachment);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('attachment:select')")
    @ApiOperation("查询附件信息")
    @PostMapping("selectAttachment")
    public ApiResult selectAttachment(@RequestBody Attachment attachment) {
        return attachmentService.selectAttachment(attachment);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('attachment:delete')")
    @ApiOperation("删除附件信息")
    @PostMapping("deleteAttachment")
    public ApiResult deleteAttachment(@RequestBody List<Attachment> attachmentList) {
        return attachmentService.deleteAttachment(attachmentList);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('attachment:download')")
    @ApiOperation("附件下载")
    @PostMapping("download")
    public void download(HttpServletResponse httpServletResponse, @RequestBody List<Attachment> attachmentList) {
        attachmentService.download(httpServletResponse, attachmentList);
    }

    @OperateLogAnnotation
    @PreAuthorize("@pu.hasAllPermissions('attachment:select')")
    @ApiOperation("获取附件的BASE64")
    @GetMapping("getFileBase64")
    public ApiResult getFileBase64(@RequestParam String source, @RequestParam String filePath) {
        return attachmentService.getFileBase64(source, filePath);
    }
}