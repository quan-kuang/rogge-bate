package com.loyer.modules.tools.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Jedis实体类
 *
 * @author kuangq
 * @date 2021-09-08 16:33
 */
@Data
@NoArgsConstructor
@ApiModel("Jedis实体类")
public class JedisEntity {

    @ApiModelProperty("类型")
    private String type;

    @NotBlank(message = "ip：地址不能为空")
    @ApiModelProperty("ip地址")
    private String ip;

    @Max(message = "port不合法，不能大于100000", value = 100000)
    @Min(message = "port不合法，不能小于1000", value = 1000)
    @NotNull(message = "port：端口号不能为空")
    @ApiModelProperty("端口号")
    private Integer port;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("过期时间")
    @Min(message = "expireTime：过期时间不合法，不能小于-1", value = -1)
    private Integer expireTime = 600;

    @ApiModelProperty("写入数据")
    private Object writeData;

    @Max(message = "writeCount：写入条数不合法，不能大于10000000", value = 10000000)
    @Min(message = "writeCount：写入条数不合法，不能小于1", value = 1)
    @ApiModelProperty("写入条数")
    private Integer writeCount = 100000;

    @ApiModelProperty("配置信息")
    private Map<String, String> config;

    public JedisEntity(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public JedisEntity(String ip, Integer port, String password) {
        this.ip = ip;
        this.port = port;
        this.password = password;
    }
}