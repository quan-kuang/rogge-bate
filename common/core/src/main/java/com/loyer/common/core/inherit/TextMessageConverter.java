package com.loyer.common.core.inherit;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信接口处理不支持text/plain类型
 *
 * @author kuangq
 * @date 2020-11-09 23:15
 */
public class TextMessageConverter extends MappingJackson2HttpMessageConverter {

    public TextMessageConverter() {
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_PLAIN);
        mediaTypes.add(MediaType.TEXT_HTML);
        setSupportedMediaTypes(mediaTypes);
    }
}