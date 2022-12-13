package com.loyer.modules.tools.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO
 *
 * @author kuangq
 * @date 2022-12-01 17:05
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
public class CacheInfoDetails {

    @ApiModelProperty("键名")
    private String key;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("字段")
    private String field;

    @ApiModelProperty("内容")
    private Object value;

    @ApiModelProperty("新内容")
    private Object newValue;

    @ApiModelProperty("过期时间戳")
    private Long expire;

    @ApiModelProperty("过期时间")
    private String expireHum;
}