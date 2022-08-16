package com.loyer.modules.tools.utils;

import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.types.RedisClientInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author kuangq
 * @title RedisUtil
 * @description redis工具类
 * @date 2021-09-03 17:23
 */
public class RedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    private static final com.loyer.common.redis.utils.RedisUtil REDIS_UTIL = ContextUtil.getBean(com.loyer.common.redis.utils.RedisUtil.class);

    //当前redis链接
    private static final RedisConnection REDIS_CONNECTION = REDIS_UTIL.getRedisTemplate().getRequiredConnectionFactory().getConnection();

    /**
     * @param type
     * @param param
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 获取redis信息
     * @date 2021-09-08 15:29
     */
    public static ApiResult getRedis(String type, String param) {
        switch (type) {
            case "info":
                return ApiResult.success(getInfo(param));
            case "config":
                return ApiResult.success(getConfig(param));
            case "clientList":
                return ApiResult.success(getClientList());
            default:
                return ApiResult.hintEnum(HintEnum.HINT_1080, type);
        }
    }

    /**
     * @param section
     * @return java.util.Properties
     * @author kuangq
     * @description 获取redisInfo信息
     * @date 2021-09-08 15:31
     */
    public static Properties getInfo(String section) {
        if (StringUtils.isBlank(section)) {
            return REDIS_CONNECTION.info();
        }
        return REDIS_CONNECTION.info(section);
    }

    /**
     * @param pattern
     * @return java.util.Properties
     * @author kuangq
     * @description 获取redisConfig信息
     * @date 2021-09-08 15:32
     */
    public static Properties getConfig(String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return REDIS_CONNECTION.getConfig(SpecialCharsConst.ASTERISK);
        }
        return REDIS_CONNECTION.getConfig(pattern);
    }

    /**
     * @param
     * @return java.util.List<org.springframework.data.redis.core.types.RedisClientInfo>
     * @author kuangq
     * @description 获取redisClient信息
     * @date 2021-09-08 15:32
     */
    public static List<RedisClientInfo> getClientList() {
        return REDIS_CONNECTION.getClientList();
    }

    /**
     * @param config
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 设置redisConfig
     * @date 2021-09-08 15:50
     */
    public static ApiResult configSet(Map<String, String> config) {
        if (config == null || config.isEmpty()) {
            return ApiResult.hintEnum(HintEnum.HINT_1014);
        }
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, String> entry : config.entrySet()) {
            String key = entry.getKey();
            try {
                REDIS_CONNECTION.setConfig(key, entry.getValue());
                result.put(key, true);
            } catch (Exception e) {
                result.put(key, e.getMessage());
                logger.error("redis配置项{}设置异常：{}", key, e.getMessage());
            }
        }
        return ApiResult.success(result);
    }
}