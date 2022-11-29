package com.loyer.common.core.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

/**
 * 角色
 *
 * @author kuangq
 * @date 2020-08-05 09:30
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("角色实体类")
public class Role extends BaseEntity {

    @ApiModelProperty("主键")
    private String uuid;

    @ApiModelProperty("角色名称")
    private String name;

    @ApiModelProperty("权限类型")
    private String permissionType;

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

    @ApiModelProperty("更新时间")
    private Timestamp updateTime;

    @ApiModelProperty("关联菜单ID")
    private List<String> menuIds;

    @ApiModelProperty("关联部门ID")
    private List<String> deptIds;
}