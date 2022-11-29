package com.loyer.modules.system.entity;

import com.loyer.common.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 表对象说明
 *
 * @author kuangq
 * @date 2020-07-18 22:47
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("表对象说明")
public class TableExplain extends BaseEntity {

    @ApiModelProperty("模式名称")
    private String schemaName;

    @ApiModelProperty("主键")
    private Integer oid;

    @ApiModelProperty("表名称")
    private String tableText;

    @ApiModelProperty("表名")
    private String tableName;

    @ApiModelProperty("类名（表名的驼峰式）")
    private String className;

    @ApiModelProperty("表类型（r/普通表、i/索引、s/序列、v/视图、c/复合类型、t/TOAST表）")
    private String tableType;

    @ApiModelProperty("是否有主键")
    private Boolean isHasPkey;

    @ApiModelProperty("主键字段名")
    private String primaryKey;

    @ApiModelProperty("主键映射java字段名")
    private String primaryName;

    @ApiModelProperty("是否有索引")
    private Boolean isHasIndex;

    @ApiModelProperty("是否有触发器")
    private Boolean isHasTrigger;

    @ApiModelProperty("检查约束数目")
    private Integer hasChecks;

    @ApiModelProperty("字段数目")
    private Integer hasFields;

    @ApiModelProperty("所有者")
    private String tableOwner;

    @ApiModelProperty("字段对象")
    private List<FieldExplain> fieldExplainList;
}