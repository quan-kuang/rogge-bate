package com.loyer.modules.tools.utils;

import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.redis.utils.CacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.types.RedisClientInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * redis工具类
 *
 * @author kuangq
 * @date 2021-09-03 17:23
 */
public class RedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    private static final Environment ENVIRONMENT = ContextUtil.getBean(Environment.class);

    private static RedisConnection getConnection() {
        return CacheUtil.CLIENT.getRequiredConnectionFactory().getConnection();
    }

    /**
     * 获取redis信息
     *
     * @author kuangq
     * @date 2021-09-08 15:29
     */
    public static ApiResult getRedis(String type, String param) {
        switch (type) {
            case "host":
                return ApiResult.success(String.format("%s:%s", ENVIRONMENT.getProperty("spring.redis.host"), ENVIRONMENT.getProperty("spring.redis.port")));
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
     * 获取redisInfo信息
     *
     * @author kuangq
     * @date 2021-09-08 15:31
     */
    public static Properties getInfo(String section) {
        try (RedisConnection connection = getConnection()) {
            if (StringUtils.isBlank(section)) {
                return connection.info();
            }
            return connection.info(section);
        }
    }

    /**
     * 获取redisConfig信息
     *
     * @author kuangq
     * @date 2021-09-08 15:32
     */
    public static Properties getConfig(String pattern) {
        try (RedisConnection connection = getConnection()) {
            if (StringUtils.isBlank(pattern)) {
                return connection.getConfig(SpecialCharsConst.ASTERISK);
            }
            return connection.getConfig(pattern);
        }
    }

    /**
     * 获取redisClient信息
     *
     * @author kuangq
     * @date 2021-09-08 15:32
     */
    public static List<RedisClientInfo> getClientList() {
        try (RedisConnection connection = getConnection()) {
            return connection.getClientList();
        }
    }

    /**
     * 设置redisConfig
     *
     * @author kuangq
     * @date 2021-09-08 15:50
     */
    public static ApiResult configSet(Map<String, String> config) {
        if (config == null || config.isEmpty()) {
            return ApiResult.hintEnum(HintEnum.HINT_1014);
        }
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, String> entry : config.entrySet()) {
            String key = entry.getKey();
            try (RedisConnection connection = getConnection()) {
                connection.setConfig(key, entry.getValue());
                result.put(key, true);
            } catch (Exception e) {
                result.put(key, e.getMessage());
                logger.error("redis配置项{}设置异常：{}", key, e.getMessage());
            }
        }
        return ApiResult.success(result);
    }
}