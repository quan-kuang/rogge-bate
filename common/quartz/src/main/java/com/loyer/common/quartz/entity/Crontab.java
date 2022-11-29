package com.loyer.common.quartz.entity;

import com.loyer.common.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 定时任务
 *
 * @author kuangq
 * @date 2020-12-16 21:58
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("定时任务实体类")
public class Crontab extends BaseEntity implements Serializable {

    @ApiModelProperty("主键")
    private String uuid;

    @ApiModelProperty("任务名称")
    private String name;

    @ApiModelProperty("任务类型")
    private String type;

    @ApiModelProperty("调用目标")
    private String invokeTarget;

    @ApiModelProperty("corn表达式")
    private String expression;

    @ApiModelProperty("是否并发")
    private Boolean concurrent;

    @ApiModelProperty("是否启用")
    private Boolean status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建者ID")
    private String creatorId;

    @ApiModelProperty("创建者名称")
    private String creatorName;

    @ApiModelProperty("创建时间")
    private Timestamp createTime;

    @ApiModelProperty("更新时间")
    private Timestamp updateTime;

    @ApiModelProperty("下次执行时间")
    private String executeTime;
}