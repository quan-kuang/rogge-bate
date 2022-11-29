package com.loyer.common.redis.utils.command;

import org.springframework.data.redis.core.ListOperations;

import java.util.List;

/**
 * list类型
 *
 * @author kuangq
 * @date 2022-08-18 19:22
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ListCommand {

    private final ListOperations listOperations;

    public ListCommand(ListOperations listOperations) {
        this.listOperations = listOperations;
    }

    public <T> void rPush(String key, T listValue) {
        listOperations.rightPush(key, listValue);
    }

    public <T> List<T> range(String key) {
        return listOperations.range(key, 0, -1);
    }

    public <T> long delete(String key, T listValue) {
        //0删除所有，>0从左至右删除，<0从右至左删除
        Long count = listOperations.remove(key, 0, listValue);
        return count == null ? 0 : count;
    }
}