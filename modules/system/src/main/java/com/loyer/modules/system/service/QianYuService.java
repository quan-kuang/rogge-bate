package com.loyer.modules.system.service;

import com.loyer.common.dedicine.entity.ApiResult;

/**
 * 浅予Service
 *
 * @author kuangq
 * @date 2023-3-21 9:51
 */
public interface QianYuService {

    ApiResult selectEcgInfo(String openId, String currentDate, int num);
}