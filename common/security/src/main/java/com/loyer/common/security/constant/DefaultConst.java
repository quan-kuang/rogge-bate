package com.loyer.common.security.constant;

/**
 * 默认常量
 *
 * @author kuangq
 * @date 2021-10-20 13:43
 */
public interface DefaultConst {

    //限流周期，单位秒
    int THROTTLING_CYCLE = 1;

    //限流每个周期内的次数
    int THROTTLING_COUNT = 10;
}