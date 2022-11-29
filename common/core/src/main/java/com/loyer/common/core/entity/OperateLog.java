package com.loyer.common.core.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 接口日志表
 *
 * @author kuangq
 * @date 2020-08-05 09:30
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("接口日志表实体类")
public class OperateLog extends BaseEntity {

    @ApiModelProperty("主键")
    private String uuid;

    @ApiModelProperty("服务端IP")
    private String serverIp;

    @ApiModelProperty("客户端IP")
    private String clientIp;

    @ApiModelProperty("请求url")
    private String url;

    @ApiModelProperty("请求类型")
    private String type;

    @ApiModelProperty("类文件路径")
    private String path;

    @ApiModelProperty("调用方法名称")
    private String method;

    @ApiModelProperty("请求入参")
    private String inArgs;

    @ApiModelProperty("响应出参")
    private String outArgs;

    @ApiModelProperty("事件标题")
    private String title;

    @ApiModelProperty("操作人ID")
    private String creatorId;

    @ApiModelProperty("操作人姓名")
    private String creatorName;

    @ApiModelProperty("操作部门ID")
    private String createDeptId;

    @ApiModelProperty("操作状态")
    private Boolean status;

    @ApiModelProperty("耗时(S)")
    private Double elapsedTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Timestamp createTime;
}