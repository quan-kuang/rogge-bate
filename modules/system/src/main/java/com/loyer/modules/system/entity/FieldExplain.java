package com.loyer.modules.system.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表字段说明
 *
 * @author kuangq
 * @date 2020-07-18 22:50
 */
@Data
@NoArgsConstructor
@ApiModel("表字段说明")
public class FieldExplain {

    @ApiModelProperty("字段名称")
    private String fieldText;

    @ApiModelProperty("字段名")
    private String fieldName;

    @ApiModelProperty("字段类型")
    private String fieldType;

    @ApiModelProperty("是否不为空")
    private Boolean isNotNull;

    @ApiModelProperty("是否有默认值")
    private Boolean isHasDefault;

    @ApiModelProperty("默认值")
    private String defaultValue;

    @ApiModelProperty("映射java字段名")
    private String javaName;

    @ApiModelProperty("映射java字段类型")
    private String javaType;
}