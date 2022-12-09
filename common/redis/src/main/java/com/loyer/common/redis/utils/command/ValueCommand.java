package com.loyer.common.redis.utils.command;

import org.springframework.data.redis.core.ValueOperations;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 *
 * @author kuangq
 * @date 2022-08-18 19:32
 */
@SuppressWarnings({"rawtypes", "ConstantConditions", "unchecked"})
public class ValueCommand {

    private final ValueOperations valueOperations;

    public ValueCommand(ValueOperations valueOperations) {
        this.valueOperations = valueOperations;
    }

    public <T> ValueOperations set(String key, T value) {
        valueOperations.set(key, value);
        return valueOperations;
    }

    public <T> ValueOperations set(String key, T value, long expireTime) {
        valueOperations.set(key, value, expireTime, TimeUnit.SECONDS);
        return valueOperations;
    }

    public <T> T get(String key) {
        ValueOperations<String, T> valueOperations = this.valueOperations;
        return valueOperations.get(key);
    }

    public <V> List<V> get(Collection<String> keys) {
        return valueOperations.multiGet(keys);
    }

    public long getExpire(String key) {
        return valueOperations.getOperations().getExpire(key);
    }

    public long incr(String key, long delta) {
        return valueOperations.increment(key, delta);
    }

    public long decr(String key, long delta) {
        return valueOperations.decrement(key, delta);
    }
}