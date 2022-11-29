package com.loyer.common.core.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 菜单
 *
 * @author kuangq
 * @date 2020-08-05 09:30
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("菜单实体类")
public class Menu extends BaseEntity {

    @ApiModelProperty("主键")
    private String uuid;

    @ApiModelProperty("菜单类型（0/目录、1/菜单、2/按钮）")
    private String type;

    @ApiModelProperty("上级ID")
    private String parentId;

    @ApiModelProperty("上级名称")
    private String parentName;

    @ApiModelProperty("菜单名称")
    private String title;

    @ApiModelProperty("组件路径")
    private String url;

    @ApiModelProperty("路由链接")
    private String path;

    @ApiModelProperty("菜单图标")
    private String icon;

    @ApiModelProperty("路由名称")
    private String name;

    @ApiModelProperty("回调地址")
    private String redirect;

    @ApiModelProperty("排序序号")
    private Integer sort;

    @ApiModelProperty("启用状态")
    private Boolean status;

    @ApiModelProperty("是否需要认证")
    private Boolean requireAuth;

    @ApiModelProperty("是否隐藏")
    private Boolean hide;

    @ApiModelProperty("权限标识")
    private String permission;

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

    @ApiModelProperty("菜单等级（扩展属性）")
    private Integer level;
}