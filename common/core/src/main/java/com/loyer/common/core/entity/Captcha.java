package com.loyer.common.core.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码拼图
 *
 * @author kuangq
 * @date 2020-11-13 10:36
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@ApiModel("实体类基类")
public class Captcha {

    @ApiModelProperty("验证码类型，文字/拼图")
    private String type;

    @ApiModelProperty("随机字符串")
    private String nonceStr;

    @ApiModelProperty("验证值")
    private String value;

    @ApiModelProperty("生成的画布的base64")
    private String canvasSrc;

    @ApiModelProperty("画布宽度")
    private Integer canvasWidth;

    @ApiModelProperty("画布高度")
    private Integer canvasHeight;

    @ApiModelProperty("生成的阻塞块的base64")
    private String blockSrc;

    @ApiModelProperty("阻塞块宽度")
    private Integer blockWidth;

    @ApiModelProperty("阻塞块高度")
    private Integer blockHeight;

    @ApiModelProperty("阻塞块凸凹半径")
    private Integer blockRadius;

    @ApiModelProperty("阻塞块的横轴坐标")
    private Integer blockX;

    @ApiModelProperty("阻塞块的纵轴坐标")
    private Integer blockY;

    @ApiModelProperty("图片获取位置")
    private Integer place;
}