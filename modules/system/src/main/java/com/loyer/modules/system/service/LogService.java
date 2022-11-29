package com.loyer.modules.system.service;

import com.loyer.common.core.entity.OperateLog;
import com.loyer.common.dedicine.entity.ApiResult;

import java.util.Map;

/**
 * 日志Service
 *
 * @author kuangq
 * @date 2020-06-02 16:49
 */
public interface LogService {

    ApiResult insertOperateLog(OperateLog operateLog);

    ApiResult selectOperateLog(OperateLog operateLog);

    ApiResult selectOperateLogEs(OperateLog operateLog);

    ApiResult selectOperateArgs(String uuid, boolean isEs);

    ApiResult syncOperateLog(OperateLog operateLog);

    ApiResult logWeekStat(Map<String, Object> params);

    ApiResult deleteOperateLog(String... uuids);
}