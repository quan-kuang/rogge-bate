package com.loyer.common.redis.constant;

/**
 * 默认常量
 *
 * @author kuangq
 * @date 2022-08-19 13:16
 */
public interface DefaultConst {

    //客户端名称
    String CLIENT_NAME = "rogbet";

    //锁的成功标示
    Long LOCK_SUCCESS = 1L;

    //锁的超时时间
    String EXPIRE_TIME = "100";
}