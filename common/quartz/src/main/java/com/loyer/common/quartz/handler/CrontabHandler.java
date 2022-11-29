package com.loyer.common.quartz.handler;

import com.alibaba.fastjson.JSON;
import com.loyer.common.apis.server.MessageServer;
import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.core.utils.reflect.BeanUtil;
import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.dedicine.utils.GeneralUtil;
import com.loyer.common.quartz.constant.CrontabConst;
import com.loyer.common.quartz.entity.Crontab;
import com.loyer.common.quartz.entity.CrontabLog;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

/**
 * 定时任务处理器
 *
 * @author kuangq
 * @date 2020-12-16 17:22
 */
public abstract class CrontabHandler implements Job {

    private static final Logger logger = LoggerFactory.getLogger(CrontabHandler.class);

    private static final MessageServer MESSAGE_SERVER = ContextUtil.getBean(MessageServer.class);

    //创建线程变量，存储任务开始时间
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    /**
     * 执行定时任务
     *
     * @author kuangq
     * @date 2020-12-16 20:03
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        Object source = jobExecutionContext.getMergedJobDataMap().get(CrontabConst.TASK_PROPERTIES_KEY);
        Crontab target = new Crontab();
        BeanUtil.copyBean(source, target);
        CrontabLog crontabLog = new CrontabLog();
        try {
            doBefore(target, crontabLog);
            Object result = doExecute(jobExecutionContext, target);
            doAfter(target, crontabLog, result);
        } catch (Exception e) {
            crontabLog.setStatus(false);
            String errorMessage = e.getMessage();
            if (e instanceof BusinessException) {
                errorMessage += ": " + ((BusinessException) e).getData().toString();
            }
            crontabLog.setErrorMessage(errorMessage);
            logger.error("【定时任务执行失败】{}：{}", target.getName(), errorMessage);
        } finally {
            MESSAGE_SERVER.saveCrontabLog(crontabLog);
        }
    }

    /**
     * 执行方法，具体由子类重载
     *
     * @author kuangq
     * @date 2020-12-16 20:01 执行
     */
    protected abstract Object doExecute(JobExecutionContext jobExecutionContext, Crontab crontab);

    /**
     * 定时任务执行前的处理
     *
     * @author kuangq
     * @date 2020-12-17 16:33
     */
    private void doBefore(Crontab crontab, CrontabLog crontabLog) {
        long startTime = System.currentTimeMillis();
        START_TIME.set(startTime);
        crontabLog.setUuid(GeneralUtil.getUuid());
        crontabLog.setCrontabId(crontab.getUuid());
        crontabLog.setCrontabName(crontab.getName());
        crontabLog.setCrontabType(crontab.getType());
        crontabLog.setBeginTime(new Timestamp(startTime));
    }

    /**
     * 定时任务执行后的处理
     *
     * @author kuangq
     * @date 2020-12-17 16:34
     */
    private void doAfter(Crontab crontab, CrontabLog crontabLog, Object result) {
        //记录请求时长
        Long startTime = START_TIME.get();
        START_TIME.remove();
        Double elapsedTime = DateUtil.getTdoa(startTime);
        crontabLog.setElapsedTime(elapsedTime);
        //默认调用成功
        crontabLog.setStatus(true);
        //设置任务返回结果
        if (result != null) {
            crontabLog.setResult(JSON.toJSONString(result));
        }
        logger.info("【定时任务执行时长】{}：{}", crontab.getName(), elapsedTime);
    }
}