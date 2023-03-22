package com.loyer.modules.system.entity;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 媒体列表入参校验
 *
 * @author kuangq
 * @date 2023-3-22 15:08
 */
@Data
public class ValidListVo {

    @Valid
    @NotNull(message = "mediaInfoList can not be null")
    private List<MediaInfo> mediaInfoList;
}