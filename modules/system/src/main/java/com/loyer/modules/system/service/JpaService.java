package com.loyer.modules.system.service;

import com.loyer.common.dedicine.entity.ApiResult;

import java.util.Map;

/**
 * TODO
 *
 * @author kuangq
 * @date 2019-11-05 17:05
 */
public interface JpaService {

    ApiResult decryptCard(Map<String, Object> params);

    ApiResult findByAcountId(String acountId);

    ApiResult findByRealNameLike(String realName);
}