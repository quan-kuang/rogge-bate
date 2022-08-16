package com.loyer.modules.tools.service.impl;

import com.loyer.common.core.utils.common.CheckParamsUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.redis.utils.RedisUtil;
import com.loyer.modules.tools.service.DemoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author kuangq
 * @title DemoServiceImpl
 * @description 示例ServiceImpl
 * @date 2021-03-06 21:20
 */
@Service
public class DemoServiceImpl implements DemoService {

    @Resource
    private RedisUtil redisUtil;

    /**
     * @param key
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 获取缓存数据
     * @date 2021-03-06 21:22
     */
    @Override
    public ApiResult getValue(String key) {
        Object result = null;
        if (redisUtil.isExist(key)) {
            result = redisUtil.getValue(key);
        }
        return ApiResult.success(result);
    }

    /**
     * @param params
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 设置缓存数据
     * @date 2021-03-06 21:22
     */
    @Override
    public ApiResult setValue(Map<String, Object> params) {
        String[] strings = {"key", "value"};
        CheckParamsUtil.checkMap(params, strings);
        long expireTime = -1;
        if (params.containsKey("expireTime")) {
            Object object = params.get("expireTime");
            if (object != null && Integer.class.getName().equals(object.getClass().getName())) {
                expireTime = Long.parseLong(object.toString());
            }
        } else {
            expireTime = 3600L;
        }
        if (expireTime == -1) {
            redisUtil.setValue(params.get("key").toString(), params.get("value"));
        } else {
            redisUtil.setValue(params.get("key").toString(), params.get("value"), expireTime);
        }
        return ApiResult.success();
    }
}
