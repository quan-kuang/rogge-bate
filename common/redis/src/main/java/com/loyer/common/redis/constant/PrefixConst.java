package com.loyer.common.redis.constant;

/**
 * 前缀常量
 *
 * @author kuangq
 * @date 2020-11-04 22:42
 */
public interface PrefixConst {

    //缓存分布式锁的前缀
    String DISTRIBUTED_LOCK = "distributedLock:";

    //控制台日志的前缀
    String CONSOLE_LOG = "consoleLog:";
}