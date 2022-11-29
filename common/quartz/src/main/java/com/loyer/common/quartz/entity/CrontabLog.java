package com.loyer.common.quartz.entity;

import com.loyer.common.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 定时任务日志
 *
 * @author kuangq
 * @date 2020-12-16 21:58
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("定时任务日志实体类")
public class CrontabLog extends BaseEntity {

    @ApiModelProperty("主键")
    private String uuid;

    @ApiModelProperty("任务ID")
    private String crontabId;

    @ApiModelProperty("任务名称")
    private String crontabName;

    @ApiModelProperty("任务类型")
    private String crontabType;

    @ApiModelProperty("运行结果")
    private String result;

    @ApiModelProperty("执行状态")
    private Boolean status;

    @ApiModelProperty("异常信息")
    private String errorMessage;

    @ApiModelProperty("开始时间")
    private Timestamp beginTime;

    @ApiModelProperty("耗时(S)")
    private Double elapsedTime;
}