package com.loyer.modules.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * vsFtpd文件服务器配置
 *
 * @author kuangq
 * @date 2020-05-27 15:52
 */
@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "vs-ftpd")
public class VsFtpd {

    //ip地址
    private String url;
    //端口号（默认21）
    private Integer port;
    //用户名（root用户注意需开放权限，不推荐使用）
    private String username;
    //密码
    private String password;
    //字符集
    private String unicode;
    //超时时间（单位毫秒）
    private Integer timeout;
}