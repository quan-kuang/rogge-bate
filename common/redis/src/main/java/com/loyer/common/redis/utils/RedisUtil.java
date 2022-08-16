package com.loyer.common.redis.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.loyer.common.redis.realize.FastJson2JsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author kuangq
 * @title RedisUtil
 * @description redis工具类
 * @date 2019-08-02 10:08
 */
@SuppressWarnings({"unused", "ConstantConditions", "rawtypes", "unchecked", "UnusedReturnValue"})
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * @param redisConnectionFactory
     * @return org.springframework.data.redis.core.RedisTemplate
     * @author kuangq
     * @description 自定义序列化, 处理redisTemplate默认存储二进制字节码问题
     * @date 2019-08-27 22:13
     */
    @Bean
    private RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //使用自定义FastJson2JsonRedisSerializer替换默认序列化（Jackson2JsonRedisSerialize）
        FastJson2JsonRedisSerializer<?> fastJson2JsonRedisSerializer = new FastJson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
        fastJson2JsonRedisSerializer.setObjectMapper(objectMapper);
        //设置key和value的序列化规则
        redisTemplate.setValueSerializer(fastJson2JsonRedisSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * @param
     * @return org.springframework.data.redis.core.RedisTemplate
     * @author kuangq
     * @description 获取RedisTemplate
     * @date 2021-09-03 17:27
     */
    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * @param key
     * @param value
     * @return org.springframework.data.redis.core.ValueOperations<java.lang.String, T>
     * @author kuangq
     * @description 缓存key值
     * @date 2020-08-14 11:21
     */
    public <T> ValueOperations<String, T> setValue(final String key, final T value) {
        ValueOperations<String, T> operations = redisTemplate.opsForValue();
        operations.set(key, value);
        return operations;
    }

    /**
     * @param key
     * @param value
     * @param expireTime
     * @return org.springframework.data.redis.core.ValueOperations<java.lang.String, T>
     * @author kuangq
     * @description 缓存指定过期时间的key值
     * @date 2020-08-14 11:21
     */
    public <T> ValueOperations<String, T> setValue(final String key, final T value, final Long expireTime) {
        ValueOperations<String, T> operations = redisTemplate.opsForValue();
        operations.set(key, value, expireTime, TimeUnit.SECONDS);
        return operations;
    }

    /**
     * @param key
     * @return T
     * @author kuangq
     * @description 获取指定key值
     * @date 2020-08-14 11:21
     */
    public <T> T getValue(final String key) {
        ValueOperations<String, T> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }

    /**
     * @param key
     * @param expireTime
     * @return boolean
     * @author kuangq
     * @description 设置key值缓存时间
     * @date 2020-08-14 11:22
     */
    public boolean setExpireTime(final String key, final Long expireTime) {
        return redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
    }

    /**
     * @param key
     * @return boolean
     * @author kuangq
     * @description 判断key值是否存在
     * @date 2020-08-14 11:22
     */
    public boolean isExist(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * @param key
     * @return boolean
     * @author kuangq
     * @description 删除指定key值
     * @date 2020-08-14 11:23
     */
    public boolean delete(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * @param collection
     * @return long
     * @author kuangq
     * @description 批量删除key值
     * @date 2020-08-14 11:23
     */
    public long delete(final Collection<?> collection) {
        return redisTemplate.delete(collection);
    }

    /**
     * @param key
     * @param listValue
     * @return void
     * @author kuangq
     * @description 缓存list数据
     * @date 2020-08-27 21:14
     */
    public <T> void setList(final String key, final T listValue) {
        redisTemplate.opsForList().rightPush(key, listValue);
    }

    /**
     * @param key
     * @return java.util.List<T>
     * @author kuangq
     * @description 获取list数据
     * @date 2020-08-14 11:23
     */
    public <T> List<T> getList(final String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * @param key
     * @param number    =0删除所有，>0从左至右删除，<0从右至左删除
     * @param listValue
     * @return long
     * @author kuangq
     * @description 删除list中值等于listValue的元素
     * @date 2020-08-27 20:57
     */
    public <T> long deleteList(final String key, final long number, final T listValue) {
        Long count = redisTemplate.opsForList().remove(key, number, listValue);
        return count == null ? 0 : count;
    }

    /**
     * @param key
     * @param setValue
     * @return long
     * @author kuangq
     * @description 缓存set数据
     * @date 2020-08-14 11:23
     */
    public <T> long setSet(final String key, final T setValue) {
        Long count = redisTemplate.opsForSet().add(key, setValue);
        return count == null ? 0 : count;
    }

    /**
     * @param key
     * @return java.util.Set<T>
     * @author kuangq
     * @description 获取set数据
     * @date 2020-08-14 11:24
     */
    public <T> Set<T> getSet(final String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * @param key
     * @param setValue
     * @return long
     * @author kuangq
     * @description 删除指定set值
     * @date 2020-08-27 20:49
     */
    public <T> long deleteSet(final String key, final T setValue) {
        Long count = redisTemplate.opsForSet().remove(key, setValue);
        return count == null ? 0 : count;
    }

    /**
     * @param key
     * @param mapKey
     * @param mapValue
     * @return void
     * @author kuangq
     * @description 缓存map数据
     * @date 2020-08-27 18:00
     */
    public <T> void setMap(final String key, final T mapKey, final T mapValue) {
        redisTemplate.opsForHash().put(key, mapKey, mapValue);
    }

    /**
     * @param key
     * @return java.util.Map<java.lang.String, T>
     * @author kuangq
     * @description 获取map数据
     * @date 2020-08-14 11:23
     */
    public <T> Map<String, T> getMap(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * @param key
     * @param mapKey
     * @return java.lang.Object
     * @author kuangq
     * @description 获取map指定key
     * @date 2021-11-23 16:29
     */
    public <T> Object getMap(final String key, final T mapKey) {
        return redisTemplate.opsForHash().get(key, mapKey);
    }

    /**
     * @param key
     * @param mapKey
     * @return long
     * @author kuangq
     * @description 删除map指定key
     * @date 2020-08-27 21:01
     */
    public <T> long deleteMap(final String key, final String mapKey) {
        return redisTemplate.opsForHash().delete(key, mapKey);
    }

    /**
     * @param prefix
     * @return java.util.List<T>
     * @author kuangq
     * @description 根据key值进行模糊查询
     * @date 2020-08-23 22:48
     */
    public <T> List<T> fuzzyQuery(final String prefix) {
        Set<String> keys = redisTemplate.keys("*" + prefix + "*");
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * @param key
     * @param delta
     * @return long
     * @author kuangq
     * @description 递增
     * @date 2020-08-27 15:58
     */
    public long increment(final String key, final long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * @param key
     * @param delta
     * @return long
     * @author kuangq
     * @description 递减
     * @date 2020-09-28 14:06
     */
    public long decrement(final String key, final long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }
}