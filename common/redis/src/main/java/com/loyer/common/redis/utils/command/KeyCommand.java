package com.loyer.common.redis.utils.command;

import com.loyer.common.redis.constant.DefaultConst;
import com.loyer.common.redis.constant.PrefixConst;
import com.loyer.common.redis.utils.CacheUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 基础命令
 *
 * @author kuangq
 * @date 2022-08-18 21:59
 */
@SuppressWarnings({"rawtypes", "ConstantConditions", "unchecked"})
public class KeyCommand {

    private final RedisTemplate redisTemplate;

    public KeyCommand(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean expire(String key, Long expireTime) {
        return redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    public boolean has(String key) {
        return redisTemplate.hasKey(key);
    }

    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public <T> long delete(Collection<T> keys) {
        return redisTemplate.delete(keys);
    }

    public Set<String> getKeys(String key) {
        return redisTemplate.keys(String.format("*%s*", key));
    }

    public <T> Object execute(Class<T> resultType, String script, String key, Object... args) {
        RedisScript<T> redisScript = new DefaultRedisScript<>(script, resultType);
        Object[] values = Arrays.stream(args).map(Object::toString).toArray(String[]::new);
        List<String> keys = Collections.singletonList(String.format("%s%s", PrefixConst.DISTRIBUTED_LOCK, key));
        return redisTemplate.execute(redisScript, new StringRedisSerializer(), new StringRedisSerializer(), keys, values);
    }

    public boolean lock(String key, String... value) {
        String script = "if redis.call('setNx',KEYS[1],ARGV[1]) then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";
        Object[] args = value.length == 1 ? new Object[]{value[0], DefaultConst.EXPIRE_TIME} : value;
        Object result = CacheUtil.KEY.execute(Long.class, script, key, args);
        return DefaultConst.LOCK_SUCCESS.equals(result);
    }

    public boolean release(String key, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = CacheUtil.KEY.execute(Long.class, script, key, value);
        return DefaultConst.LOCK_SUCCESS.equals(result);
    }
}