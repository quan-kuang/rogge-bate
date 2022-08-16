package com.loyer.common.redis.utils;

import com.loyer.common.redis.constant.PrefixConst;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Objects;

/**
 * @author kuangq
 * @title RedisDistributedLock
 * @description 基于redisTemplate的分布式锁（为保证其原子性借助于Redis的Lua脚本，采用Script脚本实现）
 * @date 2020-09-28 17:25
 */
@SuppressWarnings({"rawtypes", "unchecked", "FieldCanBeLocal"})
@Component
public class RedisDistributedLock {

    //锁的成功标示
    private final String LOCK_SUCCESS = "1";

    //锁的超时时间
    private final int EXPIRE_TIME = 10;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * @param value
     * @return java.lang.Boolean
     * @author kuangq
     * @description 加锁
     * @date 2020-10-02 12:42
     */
    public Boolean lock(String value) {
        String script = "if redis.call('setNx',KEYS[1],ARGV[1]) then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Object result = redisTemplate.execute(redisScript, new StringRedisSerializer(), new StringRedisSerializer(), Collections.singletonList(PrefixConst.DISTRIBUTED_LOCK), value, String.valueOf(EXPIRE_TIME));
        return LOCK_SUCCESS.equals(Objects.requireNonNull(result).toString());
    }

    /**
     * @param value
     * @return boolean
     * @author kuangq
     * @description 释放
     * @date 2020-10-02 12:42
     */
    public boolean release(String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Object result = redisTemplate.execute(redisScript, new StringRedisSerializer(), new StringRedisSerializer(), Collections.singletonList(PrefixConst.DISTRIBUTED_LOCK), value);
        return LOCK_SUCCESS.equals(Objects.requireNonNull(result).toString());
    }
}