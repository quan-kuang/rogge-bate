package com.loyer.modules.tools.service.impl;

import com.loyer.common.core.utils.common.CheckParamsUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.redis.utils.CacheUtil;
import com.loyer.modules.tools.service.DemoService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 示例ServiceImpl
 *
 * @author kuangq
 * @date 2021-03-06 21:20
 */
@Service
public class DemoServiceImpl implements DemoService {

    /**
     * 获取缓存数据
     *
     * @author kuangq
     * @date 2021-03-06 21:22
     */
    @Override
    public ApiResult getValue(String key) {
        Object result = null;
        if (CacheUtil.KEY.has(key)) {
            result = CacheUtil.STRING.get(key);
        }
        return ApiResult.success(result);
    }

    /**
     * 设置缓存数据
     *
     * @author kuangq
     * @date 2021-03-06 21:22
     */
    @Override
    public ApiResult setValue(Map<String, Object> params) {
        String[] strings = {"key", "value"};
        CheckParamsUtil.checkMap(params, strings);
        int expireTime = -1;
        if (params.containsKey("expireTime")) {
            Object object = params.get("expireTime");
            if (object != null && Integer.class.getName().equals(object.getClass().getName())) {
                expireTime = Integer.parseInt(object.toString());
            }
        } else {
            expireTime = 3600;
        }
        if (expireTime == -1) {
            CacheUtil.STRING.set(params.get("key").toString(), params.get("value"));
        } else {
            CacheUtil.STRING.set(params.get("key").toString(), params.get("value"), expireTime);
        }
        return ApiResult.success();
    }
}