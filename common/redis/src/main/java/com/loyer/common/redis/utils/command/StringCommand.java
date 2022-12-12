package com.loyer.common.redis.utils.command;

import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

/**
 * String类型
 *
 * @author kuangq
 * @date 2022-08-18 19:32
 */
@SuppressWarnings({"rawtypes", "ConstantConditions", "unchecked"})
public class StringCommand {

    private final ValueOperations valueOperations;

    public StringCommand(ValueOperations valueOperations) {
        this.valueOperations = valueOperations;
    }

    /**
     * 将值value关联到key
     *
     * @author kuangq
     * @date 2022-12-12 17:53
     */
    public <T> ValueOperations set(String key, T value) {
        valueOperations.set(key, value);
        return valueOperations;
    }

    /**
     * 在key不存在时设置key的值
     *
     * @author kuangq
     * @date 2022-12-12 18:06
     */
    public <T> Boolean setNx(String key, T value) {
        ValueOperations<String, T> valueOperations = this.valueOperations;
        return valueOperations.setIfAbsent(key, value);
    }

    /**
     * 将值value关联到key，并将key的过期时间，单位为妙
     *
     * @author kuangq
     * @date 2022-12-12 17:53
     */
    public <T> ValueOperations set(String key, T value, Integer expireTime) {
        valueOperations.set(key, value, Duration.ofSeconds(expireTime));
        return valueOperations;
    }

    /**
     * 将值value关联到key，并将key的过期时间
     *
     * @author kuangq
     * @date 2022-12-12 17:53
     */
    public <T> ValueOperations set(String key, T value, Duration expireTime) {
        valueOperations.set(key, value, expireTime);
        return valueOperations;
    }

    /**
     * 如果key已经存在并且是一个字符串，将value追加到key原来的值的末尾
     *
     * @author kuangq
     * @date 2022-12-12 17:53
     */
    public Integer append(String key, String value) {
        return valueOperations.append(key, value);
    }

    /**
     * 返回指定key的字符串长度
     *
     * @author kuangq
     * @date 2022-12-12 18:07
     */
    public long length(String key) {
        return valueOperations.size(key);
    }

    /**
     * 获取指定key的值
     *
     * @author kuangq
     * @date 2022-12-12 17:54
     */
    public <T> T get(String key) {
        ValueOperations<String, T> valueOperations = this.valueOperations;
        return valueOperations.get(key);
    }

    /**
     * 批量获取指定key的值
     *
     * @author kuangq
     * @date 2022-12-12 17:54
     */
    public <V> List<V> get(Collection<String> keys) {
        return valueOperations.multiGet(keys);
    }

    /**
     * 将给定key的值设为value，并返回key的旧值
     *
     * @author kuangq
     * @date 2022-12-12 17:54
     */
    public <T> T getAndSet(String key, T value) {
        ValueOperations<String, T> valueOperations = this.valueOperations;
        return valueOperations.getAndSet(key, value);
    }

    /**
     * 获取指定key的过期时间
     *
     * @author kuangq
     * @date 2022-12-12 17:55
     */
    public long getExpire(String key) {
        return valueOperations.getOperations().getExpire(key);
    }

    /**
     * 将key所储存的值加上给定的增量值
     *
     * @author kuangq
     * @date 2022-12-12 17:56
     */
    public long incr(String key, long delta) {
        return valueOperations.increment(key, delta);
    }

    /**
     * 将key所储存的值减去给定的增量值
     *
     * @author kuangq
     * @date 2022-12-12 17:56
     */
    public long decr(String key, long delta) {
        return valueOperations.decrement(key, delta);
    }
}