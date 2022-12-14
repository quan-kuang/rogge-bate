package com.loyer.modules.system.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author kuangq
 * @title WeChatConfig
 * @description 微信公众号接口配置
 * @date 2019-08-28 16:03
 */
@Data
@NoArgsConstructor
@ApiModel("微信公众号接口配置")
@Configuration
@ConfigurationProperties(prefix = "we-chat-config")
public class WeChatConfig {

    @ApiModelProperty("公众号的appid")
    private String appId;

    @ApiModelProperty("公众号的appSecret")
    private String appSecret;

    @ApiModelProperty("获取token的接口地址")
    private String getTokenUrl;

    @ApiModelProperty("获取ticket的接口地址")
    private String getTicketUrl;

    @ApiModelProperty("获取媒体文件的接口地址")
    private String getMediaUrl;

    @ApiModelProperty("微信网页授权获取CODE的接口地址")
    private String getAuthCodeUrl;

    @ApiModelProperty("微信网页授权获取AccessToken的接口地址")
    private String getAccessTokenUrl;

    @ApiModelProperty("微信网页授权获取用户信息的接口地址")
    private String getUserInfoUrl;
}