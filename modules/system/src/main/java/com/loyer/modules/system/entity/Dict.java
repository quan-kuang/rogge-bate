package com.loyer.modules.system.entity;

import com.loyer.common.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 字典
 *
 * @author kuangq
 * @date 2020-08-05 09:30
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("字典实体类")
public class Dict extends BaseEntity {

    @ApiModelProperty("主键")
    private String uuid;

    @ApiModelProperty("字典键值")
    private String value;

    @ApiModelProperty("字典名称")
    private String text;

    @ApiModelProperty("上级ID")
    private String parentId;

    @ApiModelProperty("上级名称")
    private String parentName;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("启用状态")
    private Boolean status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建人")
    private String creatorId;

    @ApiModelProperty("创建人名称")
    private String creatorName;

    @ApiModelProperty("创建时间")
    private Timestamp createTime;

    @ApiModelProperty("修改时间")
    private Timestamp updateTime;

    @ApiModelProperty("字典等级（扩展属性）")
    private Integer level;
}