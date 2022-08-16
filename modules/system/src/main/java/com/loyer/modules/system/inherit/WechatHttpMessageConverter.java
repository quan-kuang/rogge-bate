package com.loyer.modules.system.inherit;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kuangq
 * @title WechatHttpMessageConverter
 * @description 微信接口处理不支持text/plain类型
 * @date 2020-11-09 23:15
 */
public class WechatHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    public WechatHttpMessageConverter() {
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        mediaTypes.add(MediaType.TEXT_HTML);
        setSupportedMediaTypes(mediaTypes);
    }
}