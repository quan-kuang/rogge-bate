package com.loyer.common.redis.utils.command;

import org.springframework.data.redis.core.ZSetOperations;

/**
 * zSet类型
 *
 * @author kuangq
 * @date 2023-01-06 14:52
 */
public class ZSetCommand {

    private final ZSetOperations zSetOperations;

    public ZSetCommand(ZSetOperations zSetOperations) {
        this.zSetOperations = zSetOperations;
    }
}