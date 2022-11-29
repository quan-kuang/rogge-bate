package com.loyer.modules.message.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮件实体类
 *
 * @author kuangq
 * @date 2019-12-27 14:50
 */
@Data
@NoArgsConstructor
public class Mail {

    private String receiver;

    private String subject;

    private String content;

    private String image;

    private String attachment;
}