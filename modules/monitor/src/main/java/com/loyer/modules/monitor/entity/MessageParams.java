package com.loyer.modules.monitor.entity;

import com.google.common.base.Joiner;
import lombok.Data;

import java.util.List;

/**
 * 服务异常报警短信实体类
 *
 * @author kuangq
 * @date 2022-08-24 18:26
 */
@Data
public class MessageParams {

    private String templateId = "1521597";

    private String phoneNumbers = "18829895596";

    private String templateParams;

    public static MessageParams of(List<String> messageList) {
        MessageParams messageParams = new MessageParams();
        messageParams.setTemplateParams(Joiner.on("&").join(messageList));
        return messageParams;
    }
}