package com.loyer.common.redis.utils.command;

import org.springframework.data.redis.core.ListOperations;

import java.time.Duration;
import java.util.List;

/**
 * list类型
 *
 * @author kuangq
 * @date 2022-08-18 19:22
 */
@SuppressWarnings({"rawtypes", "unchecked", "UnusedReturnValue"})
public class ListCommand {

    private final ListOperations listOperations;

    public ListCommand(ListOperations listOperations) {
        this.listOperations = listOperations;
    }

    /**
     * 获取列表长度
     *
     * @author kuangq
     * @date 2022-12-12 16:23
     */
    public long length(String key) {
        return handle(listOperations.size(key));
    }

    /**
     * 通过索引设置列表元素的值
     *
     * @author kuangq
     * @date 2022-12-12 14:51
     */
    public <T> void set(String key, int index, T value) {
        listOperations.set(key, index, value);
    }

    /**
     * 在列表头部添加一个或多个值
     *
     * @author kuangq
     * @date 2022-12-12 13:40
     */
    public <T> long lPush(String key, T... value) {
        return handle(listOperations.leftPushAll(key, value));
    }

    /**
     * 在列表尾部添加一个或多个值
     *
     * @author kuangq
     * @date 2022-12-12 13:40
     */
    public <T> long rPush(String key, T... value) {
        return handle(listOperations.rightPushAll(key, value));
    }

    /**
     * 在列表的元素前插入元素
     *
     * @author kuangq
     * @date 2022-12-12 16:45
     */
    public <T> long lPush(String key, T pivot, T value) {
        return handle(listOperations.leftPush(key, pivot, value));
    }

    /**
     * 在列表的元素后插入元素
     *
     * @author kuangq
     * @date 2022-12-12 16:45
     */
    public <T> long rPush(String key, T pivot, T value) {
        return handle(listOperations.rightPush(key, pivot, value));
    }

    /**
     * 获取列表指定范围内的元素
     *
     * @author kuangq
     * @date 2022-12-12 13:41
     */
    public <T> List<T> range(String key) {
        return listOperations.range(key, 0, -1);
    }

    /**
     * 移除列表元素
     *
     * @author kuangq
     * @date 2022-12-12 13:46
     */
    public <T> long delete(String key, T value) {
        //0删除所有，>0从左至右删除，<0从右至左删除
        return handle(listOperations.remove(key, 0, value));
    }

    /**
     * 通过索引获取列表中的元素
     *
     * @author kuangq
     * @date 2022-12-12 16:36
     */
    public Object index(String key, int index) {
        return listOperations.index(key, index);
    }

    /**
     * 移出并获取列表的第一个元素
     *
     * @author kuangq
     * @date 2022-12-12 16:36
     */
    public Object lPop(String key) {
        return listOperations.leftPop(key);
    }

    /**
     * 移除并获取列表最后一个元素
     *
     * @author kuangq
     * @date 2022-12-12 16:36
     */
    public Object rPop(String key) {
        return listOperations.rightPop(key);
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @author kuangq
     * @date 2022-12-12 16:36
     */
    public Object lPop(String key, Duration duration) {
        return listOperations.leftPop(key, duration);
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     *
     * @author kuangq
     * @date 2022-12-12 16:36
     */
    public Object rPop(String key, Duration duration) {
        return listOperations.rightPop(key, duration);
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