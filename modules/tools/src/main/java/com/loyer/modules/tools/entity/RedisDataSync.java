package com.loyer.modules.tools.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * redis数据同步
 *
 * @author kuangq
 * @date 2022-01-27 10:59
 */
@Data
@NoArgsConstructor
@ApiModel("redis数据同步实体类")
public class RedisDataSync {

    @ApiModelProperty("日志路径")
    private String logFilePath;

    @ApiModelProperty("系统端口号")
    private Integer systemProfilePort;

    @ApiModelProperty("应用端口号")
    private Integer httpProfilePort;

    @ApiModelProperty("源redis类型")
    private String sourceType;

    @ApiModelProperty("源redis地址")
    private String sourceAddress;

    @ApiModelProperty("源redis密码")
    private String sourcePassword;

    @ApiModelProperty("目标redis类型")
    private String targetType;

    @ApiModelProperty("目标redis地址")
    private String targetAddress;

    @ApiModelProperty("目标redis密码")
    private String targetPassword;
}