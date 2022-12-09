package com.loyer.modules.tools.entity;

import com.loyer.common.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 缓存列表
 *
 * @author kuangq
 * @date 2022-12-01 11:09
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("缓存列表")
public class CacheInfo extends BaseEntity {

    @ApiModelProperty("树形")
    private Boolean formatTree;

    @ApiModelProperty("键名")
    private String key;

    @ApiModelProperty("父级")
    private String parent;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("类型")
    private String type;
}