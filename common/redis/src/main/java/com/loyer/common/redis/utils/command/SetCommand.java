package com.loyer.common.redis.utils.command;

import org.springframework.data.redis.core.SetOperations;

import java.util.List;
import java.util.Set;

/**
 * set类型
 *
 * @author kuangq
 * @date 2022-08-18 19:05
 */
@SuppressWarnings({"rawtypes", "unchecked", "ConfusingArgumentToVarargsMethod"})
public class SetCommand {

    private final SetOperations setOperations;

    public SetCommand(SetOperations setOperations) {
        this.setOperations = setOperations;
    }

    /**
     * 向集合添加一个或多个成员
     *
     * @author kuangq
     * @date 2022-12-12 17:52
     */
    public <T> long add(String key, T... value) {
        return handle(setOperations.add(key, value));
    }

    /**
     * 判断member元素是否是集合key的成员
     *
     * @author kuangq
     * @date 2022-12-12 17:52
     */
    public <T> boolean isMember(String key, T value) {
        return Boolean.TRUE.equals(setOperations.isMember(key, value));
    }

    /**
     * 返回集合中的所有成员
     *
     * @author kuangq
     * @date 2022-12-12 17:52
     */
    public <T> Set<T> members(String key) {
        return setOperations.members(key);
    }

    /**
     * 返回集合中一个随机数
     *
     * @author kuangq
     * @date 2022-12-12 17:51
     */
    public Object randomMember(String key) {
        return setOperations.randomMember(key);
    }

    /**
     * 返回集合中多个随机数
     *
     * @author kuangq
     * @date 2022-12-12 17:51
     */
    public <T> List<T> randomMember(String key, long length) {
        return setOperations.randomMembers(key, length);
    }

    /**
     * 移除集合中一个或多个成员
     *
     * @author kuangq
     * @date 2022-12-12 17:51
     */
    public <T> long delete(String key, T... value) {
        return handle(setOperations.remove(key, value));
    }

    /**
     * 移除并返回集合中的一个随机元素
     *
     * @author kuangq
     * @date 2022-12-12 17:50
     */
    public Object pop(String key) {
        return setOperations.pop(key);
    }

    /**
     * 移除并返回集合中的多个随机元素
     *
     * @author kuangq
     * @date 2022-12-12 17:50
     */
    public <T> List<T> pop(String key, int length) {
        return setOperations.pop(key, length);
    }

    /**
     * 处理Long
     *
     * @author kuangq
     * @date 2022-12-12 16:52
     */
    private long handle(Long value) {
        return value == null ? 0 : value;
    }
}