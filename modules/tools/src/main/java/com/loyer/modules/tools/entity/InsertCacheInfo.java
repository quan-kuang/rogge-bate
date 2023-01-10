package com.loyer.modules.tools.entity;

import com.loyer.modules.tools.enums.Lawful;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.connection.DataType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 新增键值
 *
 * @author kuangq
 * @date 2023-01-10 11:09
 */
@Data
@NoArgsConstructor
@ApiModel("新增键值")
public class InsertCacheInfo {

    @NotBlank(message = "键名不能为空")
    @ApiModelProperty("键名")
    private String key;

    @Lawful(enumClassAry = DataType.class, message = "数据类型参数不合法")
    @NotBlank(message = "数据类型不能为空")
    @ApiModelProperty("数据类型")
    private String type;

    @Min(message = "过期时间不能小于-1", value = -1)
    @NotNull(message = "过期时间不能为空")
    @ApiModelProperty("过期时间")
    private Integer expire;

    @ApiModelProperty("键值")
    private String value;

    @ApiModelProperty("集合")
    private Set<String> set;

    @ApiModelProperty("列表")
    private List<String> list;

    @ApiModelProperty("哈希")
    private Map<String, Object> hash;
}