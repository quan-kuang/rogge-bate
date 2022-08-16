package com.loyer.modules.system.mapper.postgresql;

import com.loyer.modules.system.entity.Attachment;

import java.util.List;

/**
 * @author kuangq
 * @title AttachmentMapper
 * @description 附件Mapper
 * @date 2020-08-02 15:20
 */
public interface AttachmentMapper {

    Integer saveAttachment(Attachment attachment);

    List<Attachment> selectAttachment(Attachment attachment);

    Integer deleteAttachment(List<Attachment> attachmentList);
}