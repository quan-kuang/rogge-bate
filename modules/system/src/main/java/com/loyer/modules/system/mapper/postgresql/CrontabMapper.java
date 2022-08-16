package com.loyer.modules.system.mapper.postgresql;

import com.loyer.common.quartz.entity.Crontab;

import java.util.List;

/**
 * @author kuangq
 * @title CrontabMapper
 * @description 定时任务Mapper
 * @date 2020-12-16 21:58
 */
public interface CrontabMapper {

    Integer saveCrontab(Crontab crontab);

    List<Crontab> selectCrontab(Crontab crontab);

    List<Crontab> selectCrontabType(String... uuids);

    Integer deleteCrontab(String... uuids);
}