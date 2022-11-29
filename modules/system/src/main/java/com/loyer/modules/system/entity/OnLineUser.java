package com.loyer.modules.system.entity;

import com.loyer.common.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 在线用户
 *
 * @author kuangq
 * @date 2020-12-15 15:13
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("在线用户实体类")
public class OnLineUser extends BaseEntity {

    @ApiModelProperty("主键")
    private String uuid;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("手机")
    private String phone;

    @ApiModelProperty("部门")
    private String deptName;

    @ApiModelProperty("IP")
    private String ip;

    @ApiModelProperty("位置")
    private String position;

    @ApiModelProperty("浏览器")
    private String browser;

    @ApiModelProperty("操作系统")
    private String operateSystem;

    @ApiModelProperty("登录时间")
    private Timestamp loginTime;
}