package com.loyer.modules.system.entity;

import com.loyer.common.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 部门
 *
 * @author kuangq
 * @date 2020-09-10 13:42
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("部门实体类")
public class Dept extends BaseEntity {

    @ApiModelProperty("主键")
    private String uuid;

    @ApiModelProperty("部门名称")
    private String name;

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

    @ApiModelProperty("创建者ID")
    private String creatorId;

    @ApiModelProperty("创建者名称")
    private String creatorName;

    @ApiModelProperty("创建时间")
    private Timestamp createTime;

    @ApiModelProperty("修改时间")
    private Timestamp updateTime;
}