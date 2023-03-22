package com.loyer.modules.system.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 媒体信息
 *
 * @author kuangq
 * @date 2023-3-22 15:06
 */
@Data
@NoArgsConstructor
@ApiModel("媒体信息")
public class MediaInfo {

    @NotBlank(message = "name can not be blank")
    private String name;

    @NotBlank(message = "type can not be blank")
    private String type;

    @NotBlank(message = "base64 can not be blank")
    private String base64;
}