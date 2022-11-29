package com.loyer.modules.system.mapper.postgresql;

import com.loyer.common.core.entity.OperateLog;

import java.util.List;
import java.util.Map;

/**
 * 日志Mapper
 *
 * @author kuangq
 * @date 2020-05-27 14:51
 */
public interface LogMapper {

    Integer insertOperateLog(OperateLog operateLog);

    List<OperateLog> selectOperateLog(OperateLog operateLog);

    Map<String, Object> selectOperateArgs(String uuid);

    Integer selectOperateLogTotal(OperateLog operateLog);

    List<OperateLog> selectOperateLogList(OperateLog operateLog);

    List<Map<String, Object>> logWeekStat(Map<String, Object> params);

    Integer deleteOperateLog(String... uuids);
}