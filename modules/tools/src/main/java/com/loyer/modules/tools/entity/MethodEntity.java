package com.loyer.modules.tools.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 方法实体类
 *
 * @author kuangq
 * @date 2019-12-12 15:52
 */
@Data
@NoArgsConstructor
@ApiModel("方法实体类")
public class MethodEntity {

    @ApiModelProperty("方法权限范围")
    private String permission;

    @ApiModelProperty("返回类型")
    private String returnType;

    @ApiModelProperty("方法名称")
    private String name;

    @ApiModelProperty("方法参数类型")
    private String[] parameterTypes;

    @ApiModelProperty("注释")
    private List<Map<String, String>> annotations;
}