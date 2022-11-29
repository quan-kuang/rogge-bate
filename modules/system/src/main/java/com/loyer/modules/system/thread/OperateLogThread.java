package com.loyer.modules.system.thread;

import com.loyer.common.core.entity.OperateLog;
import com.loyer.common.datasource.utils.ElasticsearchUtil;
import com.loyer.modules.system.mapper.postgresql.LogMapper;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 接口日志查询线程
 *
 * @author kuangq
 * @date 2019-11-15 16:57
 */
public class OperateLogThread implements Callable<List<OperateLog>> {

    private final LogMapper logMapper;
    private final OperateLog operateLog;
    private final Integer index;

    public OperateLogThread(LogMapper logMapper, OperateLog operateLog, Integer index) {
        this.logMapper = logMapper;
        this.operateLog = operateLog;
        this.index = index;
    }

    @Override
    public List<OperateLog> call() {
        operateLog.setPageNum(index);
        List<OperateLog> operateLogList = logMapper.selectOperateLogList(operateLog);
        for (OperateLog log : operateLogList) {
            if (!ElasticsearchUtil.existsData("operate_log", log.getUuid())) {
                ElasticsearchUtil.insertData("operate_log", log.getUuid(), log);
            }
        }
        return operateLogList;
    }
}