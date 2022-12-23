package com.loyer.modules.system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 微信模板消息入参
 *
 * @author kuangq
 * @date 2022-12-23 8:49
 */
@Data
@NoArgsConstructor
public class TemplateMessage {

    private String userId;

    private String templateId;

    private String url;

    private String topColor;

    private List<TemplateMessage.Data> dataList;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {

        private String key;

        private String value;

        private String color;
    }
}