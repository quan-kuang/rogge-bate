package com.loyer.modules.message.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kuangq
 * @title BaiduCloudKeyPair
 * @description 百度云申请的应用密匙对
 * @date 2020-11-02 13:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaiduCloudKeyPair {

    //应用ID
    private String appId;
    //应用KEY
    private String clientId;
    //应用SECRET KEY
    private String clientSecret;
}