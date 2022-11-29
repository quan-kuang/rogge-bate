package com.loyer.modules.system.entity;

import com.loyer.common.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 附件
 *
 * @author kuangq
 * @date 2020-08-05 09:30
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("附件实体类")
public class Attachment extends BaseEntity {

    @ApiModelProperty("主键")
    private String uuid;

    @ApiModelProperty("附件名称")
    private String name;

    @ApiModelProperty("附件类型")
    private String type;

    @ApiModelProperty("媒体类型")
    private String mime;

    @ApiModelProperty("所在服务器地址")
    private String path;

    @ApiModelProperty("附件原始大小")
    private Integer rawSize;

    @ApiModelProperty("附件大小")
    private Integer size;

    @ApiModelProperty("附件内容")
    private String base64;

    @ApiModelProperty("存储源")
    private String source;

    @ApiModelProperty("关联事项主键")
    private String foreignId;

    @ApiModelProperty("关联引用类型")
    private String citeType;

    @ApiModelProperty("创建人")
    private String creatorId;

    @ApiModelProperty("创建人名称")
    private String creatorName;

    @ApiModelProperty("创建部门ID")
    private String createDeptId;

    @ApiModelProperty("创建时间")
    private Timestamp createTime;

    @ApiModelProperty("更新时间")
    private Timestamp updateTime;
}