package com.loyer.modules.system.service;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.Dict;

/**
 * 字典Service
 *
 * @author kuangq
 * @date 2020-05-16 17:03
 */
public interface DictService {

    ApiResult saveDict(Dict dict);

    ApiResult selectDict(Dict dict);

    ApiResult deleteDict(String... dictIds);

    ApiResult selectCascade(String... uuids);

    ApiResult checkDictExists(Dict dict);
}