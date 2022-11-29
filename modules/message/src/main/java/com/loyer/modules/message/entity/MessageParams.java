package com.loyer.modules.message.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 短信参数实体类
 *
 * @author kuangq
 * @date 2021-03-06 11:26
 */
@Data
@NoArgsConstructor
@ApiModel("短信参数实体类")
public class MessageParams {

    @ApiModelProperty("短信应用ID")
    private String smsSdkAppId;

    @ApiModelProperty("签名")
    private String sign;

    @ApiModelProperty("模板ID")
    private String templateId;

    @NotBlank(message = "下发手机号码不能为空")
    @ApiModelProperty("下发手机号码")
    private String phoneNumbers;

    @NotBlank(message = "内容模板参数不能为空")
    @ApiModelProperty("内容模板参数")
    private String templateParams;
}