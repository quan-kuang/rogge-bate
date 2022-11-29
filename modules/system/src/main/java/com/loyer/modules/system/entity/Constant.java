package com.loyer.modules.system.entity;

import com.loyer.common.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 系统常量实体类
 *
 * @author kuangq
 * @date 2021-11-23 14:17
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("常量信息实体类")
public class Constant extends BaseEntity {

    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("键名")
    private String key;

    @ApiModelProperty("键值")
    private String value;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Timestamp createTime;

    @ApiModelProperty("修改时间")
    private Timestamp updateTime;
}