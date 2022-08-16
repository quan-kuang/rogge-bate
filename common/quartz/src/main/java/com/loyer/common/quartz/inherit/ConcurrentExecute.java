package com.loyer.common.quartz.inherit;

import com.loyer.common.quartz.entity.Crontab;
import com.loyer.common.quartz.handle.CrontabHandler;
import com.loyer.common.quartz.utils.InvokeUtil;
import org.quartz.JobExecutionContext;

/**
 * @author kuangq
 * @title ConcurrentExecute
 * @description 定时任务执行（并发执行）
 * @date 2020-12-16 22:46
 */
public class ConcurrentExecute extends CrontabHandler {

    @Override
    protected Object doExecute(JobExecutionContext jobExecutionContext, Crontab crontab) {
        return InvokeUtil.invokeMethod(crontab.getInvokeTarget(), true);
    }
}