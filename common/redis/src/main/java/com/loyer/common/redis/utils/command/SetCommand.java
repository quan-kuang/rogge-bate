package com.loyer.common.redis.utils.command;

import org.springframework.data.redis.core.SetOperations;

import java.util.Set;

/**
 * set类型
 *
 * @author kuangq
 * @date 2022-08-18 19:05
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SetCommand {

    private final SetOperations setOperations;

    public SetCommand(SetOperations setOperations) {
        this.setOperations = setOperations;
    }

    public <T> long add(String key, T value) {
        Long count = setOperations.add(key, value);
        return count == null ? 0 : count;
    }

    public <T> boolean has(String key, T value) {
        return Boolean.TRUE.equals(setOperations.isMember(key, value));
    }

    public <T> Set<T> get(String key) {
        return setOperations.members(key);
    }

    public <T> long delete(String key, T value) {
        Long count = setOperations.remove(key, value);
        return count == null ? 0 : count;
    }
}