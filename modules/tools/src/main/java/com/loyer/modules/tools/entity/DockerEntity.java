package com.loyer.modules.tools.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * Docker实体类
 *
 * @author kuangq
 * @date 2021-11-19 17:37
 */
@Data
@NoArgsConstructor
@ApiModel("Docker实体类")
public class DockerEntity {

    @NotBlank(message = "ip：地址不能为空")
    @ApiModelProperty("ip地址")
    private String ip;

    @Max(message = "port不合法，不能大于100000", value = 100000)
    @Min(message = "port不合法，不能小于1000", value = 1000)
    @ApiModelProperty("端口号")
    private Integer port;

    @NotBlank(message = "containerId：容器ID不能为空")
    @ApiModelProperty("容器ID")
    private String containerId;

    private String localPath;

    private String remotePath;

    private String command;
}