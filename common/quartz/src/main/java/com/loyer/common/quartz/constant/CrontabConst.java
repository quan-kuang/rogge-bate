package com.loyer.common.quartz.constant;

/**
 * 任务调度通用常量
 *
 * @author kuangq
 * @date 2020-12-16 17:48
 */
public interface CrontabConst {

    //执行任务名称的前缀
    String TASK_NAME_PREFIX = "TASK_NAME_";

    //执行目标的KEY值，据此获取Job的实例
    String TASK_PROPERTIES_KEY = "TASK_PROPERTIES";
}