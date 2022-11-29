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

    public static ValueCommand VALUE;

    public static ListCommand LIST;

    public static SetCommand SET;

    public static HashCommand HASH;

    @Autowired
    public CacheUtil(RedisTemplate redisTemplate) {
        CLIENT = redisTemplate;
        KEY = new KeyCommand(redisTemplate);
        VALUE = new ValueCommand(redisTemplate.opsForValue());
        LIST = new ListCommand(redisTemplate.opsForList());
        SET = new SetCommand(redisTemplate.opsForSet());
        HASH = new HashCommand(redisTemplate.opsForHash());
    }
}