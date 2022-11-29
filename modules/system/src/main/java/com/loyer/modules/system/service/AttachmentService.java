package com.loyer.modules.system.service;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.Attachment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 附件Service
 *
 * @author kuangq
 * @date 2020-08-02 15:20
 */
public interface AttachmentService {

    ApiResult upload(HttpServletRequest httpServletRequest);

    Attachment saveAttachment(InputStream inputStream, Map<String, Object> params, Attachment attachment);

    ApiResult saveAttachment(Attachment attachment);

    ApiResult selectAttachment(Attachment attachment);

    ApiResult deleteAttachment(List<Attachment> attachmentList);

    void download(HttpServletResponse httpServletResponse, List<Attachment> attachmentList);

    ApiResult getFileBase64(String source, String filePath);
}