package com.loyer.common.redis.utils.command;

import org.springframework.data.redis.core.HashOperations;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * hash类型
 *
 * @author kuangq
 * @date 2022-08-18 19:05
 */
@SuppressWarnings({"rawtypes", "unchecked", "ConfusingArgumentToVarargsMethod"})
public class HashCommand {

    private final HashOperations hashOperations;

    public HashCommand(HashOperations hashOperations) {
        this.hashOperations = hashOperations;
    }

    /**
     * 查看哈希表中指定的字段是否存在
     *
     * @author kuangq
     * @date 2022-12-12 15:21
     */
    public <T> boolean has(String key, T filed) {
        return hashOperations.hasKey(key, filed);
    }

    /**
     * 将哈希表key中的字段field的值设为value
     *
     * @author kuangq
     * @date 2022-12-12 15:22
     */
    public <T> void put(String key, T filed, T value) {
        hashOperations.put(key, filed, value);
    }

    /**
     * 同时将多个field-value(域-值)对设置到哈希表key中
     *
     * @author kuangq
     * @date 2022-12-12 15:22
     */
    public <K, V> void putAll(String key, Map<K, V> map) {
        hashOperations.putAll(key, map);
    }

    /**
     * 获取存储在哈希表中指定字段的值
     *
     * @author kuangq
     * @date 2022-12-12 15:22
     */
    public <T> T get(String key, T filed) {
        HashOperations<String, T, T> hashOperations = this.hashOperations;
        return hashOperations.get(key, filed);
    }

    /**
     * 获取在哈希表中指定key的所有字段和值
     *
     * @author kuangq
     * @date 2022-12-12 15:22
     */
    public <T> Map<T, T> getAll(String key) {
        return hashOperations.entries(key);
    }

    /**
     * 删除一个或多个哈希表字段
     *
     * @author kuangq
     * @date 2022-12-12 15:23
     */
    public <T> long delete(String key, T... filed) {
        return hashOperations.delete(key, filed);
    }

    /**
     * 获取哈希表中字段的数量
     *
     * @author kuangq
     * @date 2022-12-12 15:26
     */
    public long length(String key) {
        return hashOperations.size(key);
    }

    /**
     * 获取所有哈希表中的字段
     *
     * @author kuangq
     * @date 2022-12-12 15:26
     */
    public <T> Set<T> keys(String key) {
        return hashOperations.keys(key);
    }

    /**
     * 获取所有哈希表中的字段
     *
     * @author 获取哈希表中所有值
     * @date 2022-12-12 15:26
     */
    public <T> List<T> values(String key) {
        return hashOperations.values(key);
    }

    /**
     * 为哈希表key中的指定字段的浮点数值加上增量increment
     *
     * @author kuangq
     * @date 2022-12-12 16:14
     */
    public <T> long increment(String key, T filed, long value) {
        return hashOperations.increment(key, filed, value);
    }

    /**
     * 为哈希表key中的指定字段的浮点数值加上增量increment
     *
     * @author kuangq
     * @date 2022-12-12 16:14
     */
    public <T> double increment(String key, T filed, double value) {
        return hashOperations.increment(key, filed, value);
    }
}