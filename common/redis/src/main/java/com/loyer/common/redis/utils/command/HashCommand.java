package com.loyer.common.redis.utils.command;

import org.springframework.data.redis.core.HashOperations;

import java.util.Map;

/**
 * hash类型
 *
 * @author kuangq
 * @date 2022-08-18 19:05
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class HashCommand {

    private final HashOperations hashOperations;

    public HashCommand(HashOperations hashOperations) {
        this.hashOperations = hashOperations;
    }

    public <T> boolean has(String key, T filed) {
        return hashOperations.hasKey(key, filed);
    }

    public <T> void put(String key, T filed, T value) {
        hashOperations.put(key, filed, value);
    }

    public <T> T get(String key, T filed) {
        HashOperations<String, T, T> hashOperations = this.hashOperations;
        return hashOperations.get(key, filed);
    }

    public <T> Map<T, T> getAll(String key) {
        return hashOperations.entries(key);
    }

    public <T> long delete(String key, T filed) {
        return hashOperations.delete(key, filed);
    }
}