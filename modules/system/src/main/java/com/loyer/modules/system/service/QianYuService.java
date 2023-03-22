package com.loyer.modules.system.service;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.MediaInfo;

import java.util.List;

/**
 * 浅予Service
 *
 * @author kuangq
 * @date 2023-3-21 9:51
 */
public interface QianYuService {

    ApiResult punchCard(String openId, String currentDate);

    ApiResult selectEcgInfo(String openId, String currentDate, int num);

    ApiResult sendImageMsg(List<MediaInfo> mediaInfoList);
}