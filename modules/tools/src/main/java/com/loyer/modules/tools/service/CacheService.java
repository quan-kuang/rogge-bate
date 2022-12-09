package com.loyer.modules.tools.service;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.tools.entity.CacheInfo;
import com.loyer.modules.tools.entity.CacheInfoDetails;

/**
 * TODO
 *
 * @author kuangq
 * @date 2022-12-01 11:15
 */
public interface CacheService {

    ApiResult selectCacheInfo(CacheInfo cacheInfo);

    ApiResult selectCacheInfoDetails(String key);

    ApiResult deleteCacheInfo(CacheInfoDetails cacheInfoDetails);

    ApiResult saveCacheInfo(CacheInfoDetails cacheInfoDetails);
}