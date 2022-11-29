package com.loyer.modules.tools.service;

import com.loyer.common.dedicine.entity.ApiResult;

import java.util.Map;

/**
 * 示例Service
 *
 * @author kuangq
 * @date 2019-07-16 15:26
 */
public interface DemoService {

    ApiResult getValue(String key);

    ApiResult setValue(Map<String, Object> params);
}