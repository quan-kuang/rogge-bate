package com.loyer.modules.system.service;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.quartz.entity.Crontab;
import com.loyer.common.quartz.entity.CrontabLog;

/**
 * 定时任务Service
 *
 * @author kuangq
 * @date 2020-12-16 21:58
 */
public interface CrontabService {

    ApiResult saveCrontab(Crontab crontab);

    ApiResult selectCrontab(Crontab crontab);

    ApiResult getExecuteTime(String expression, Integer count);

    ApiResult deleteCrontab(String... uuids);

    ApiResult checkCornExpression(String cornExpression);

    ApiResult executeCrontab(Crontab crontab);

    ApiResult selectCrontabLog(CrontabLog crontabLog);

    ApiResult deleteCrontabLog(String... uuids);

    ApiResult insertCrontabLog(CrontabLog crontabLog);
}