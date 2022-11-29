package com.loyer.common.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loyer.common.dedicine.constant.SystemConst;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

/**
 * 用户
 *
 * @author kuangq
 * @date 2020-08-05 09:30
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("用户实体类")
public class User extends BaseEntity {

    @ApiModelProperty("主键")
    private String uuid;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("身份证号")
    private String idCard;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("出生日期")
    private String birthday;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("电子邮箱")
    private String email;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("部门ID")
    private String deptId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("是否认证")
    private Boolean auth;

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

    @ApiModelProperty("最后登录时间")
    private Timestamp loginTime;

    @JsonIgnore
    @ApiModelProperty("所属角色")
    private List<Role> roleList;

    @ApiModelProperty("角色ID")
    private List<String> roleIds;

    @ApiModelProperty("用户权限范围")
    private Integer permissionScope;

    @ApiModelProperty("所属权限部门ID")
    private Set<String> permissionDeptSet;

    @ApiModelProperty("关联菜单对象")
    private List<Menu> menuList;

    /**
     * 判断是否为管理员
     *
     * @author kuangq
     * @date 2020-07-21 23:17
     */
    public boolean checkIsAdmin() {
        return SystemConst.ADMIN.equals(this.uuid) || (this.roleIds != null && this.roleIds.contains(SystemConst.ADMIN));
    }
}