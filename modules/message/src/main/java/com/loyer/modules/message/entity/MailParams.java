package com.loyer.modules.message.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 邮件参数实体类
 *
 * @author kuangq
 * @date 2021-05-29 20:49
 */
@Data
@NoArgsConstructor
@ApiModel("邮件参数实体类")
public class MailParams {

    @NotBlank(message = "发件人名称不能为空")
    @ApiModelProperty("发件人名称")
    private String sender;

    @NotBlank(message = "收件人邮箱不能为空")
    @ApiModelProperty("收件人邮箱")
    private String receiver;

    @ApiModelProperty("抄送人邮箱")
    private String carbonCopy;

    @NotBlank(message = "邮件主题不能为空")
    @ApiModelProperty("主题")
    private String subject;

    @NotBlank(message = "邮件内容不能为空")
    @ApiModelProperty("正文")
    private String body;

    @ApiModelProperty("文件资源")
    private List<FileResource> fileResourceList;

    @Data
    @NoArgsConstructor
    public static class FileResource {

        @ApiModelProperty("文件名称")
        private String name;

        @ApiModelProperty("文件类型")
        private String type;

        @ApiModelProperty("文件内容")
        private String content;

        @ApiModelProperty("插入位置")
        private String place;
    }
}