package com.loyer.common.redis.utils;

import com.loyer.common.redis.utils.command.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 缓存工具类
 *
 * @author kuangq
 * @date 2019-08-02 10:08
 */
@SuppressWarnings("rawtypes")
@Component
public class CacheUtil {

    public static RedisTemplate CLIENT;

    public static KeyCommand KEY;

    public static SetCommand SET;

    public static ListCommand LIST;

    public static ZSetCommand ZSET;

    public static HashCommand HASH;

    public static StringCommand STRING;

    @Autowired
    public CacheUtil(RedisTemplate redisTemplate) {
        CLIENT = redisTemplate;
        KEY = new KeyCommand(redisTemplate);
        SET = new SetCommand(redisTemplate.opsForSet());
        LIST = new ListCommand(redisTemplate.opsForList());
        ZSET = new ZSetCommand(redisTemplate.opsForZSet());
        HASH = new HashCommand(redisTemplate.opsForHash());
        STRING = new StringCommand(redisTemplate.opsForValue());
    }
}