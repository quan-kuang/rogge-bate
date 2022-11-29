package com.loyer.common.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 实体类基类
 *
 * @author kuangq
 * @date 2020-06-14 20:53
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties
@Data
@NoArgsConstructor
@ApiModel("实体类基类")
public class BaseEntity {

    @ApiModelProperty("认证凭据")
    private String token;

    @ApiModelProperty("分页页码")
    private Integer pageNum;

    @ApiModelProperty("分页大小")
    private Integer pageSize;

    @ApiModelProperty("查询开始时间")
    private String startTime;

    @ApiModelProperty("查询结束时间")
    private String endTime;

    @ApiModelProperty("查询条件")
    private String queryText;

    @ApiModelProperty("附加参数")
    private Map<String, Object> params;
}