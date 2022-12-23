package com.loyer.common.quartz.handler;

import com.alibaba.fastjson.JSON;
import com.loyer.common.apis.server.MessageServer;
import com.loyer.common.apis.server.SystemServer;
import com.loyer.common.core.enums.DatePattern;
import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.core.utils.reflect.BeanUtil;
import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.entity.WeChatAlarm;
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

    private static final SystemServer SYSTEM_SERVER = ContextUtil.getBean(SystemServer.class);

    private static final MessageServer MESSAGE_SERVER = ContextUtil.getBean(MessageServer.class);

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
        long startTime = System.currentTimeMillis();
        try {
            doBefore(target, crontabLog, startTime);
            Object result = doExecute(jobExecutionContext, target);
            doAfter(crontabLog, result);
        } catch (Exception e) {
            e.printStackTrace();
            crontabLog.setStatus(false);
            crontabLog.setResult(e.getMessage());
            sendWeChatAlarm(target.getName(), e.getMessage());
            logger.error("【定时任务执行失败】{}：{}", target.getName(), e.getMessage());
        } finally {
            crontabLog.setElapsedTime(DateUtil.getTdoa(startTime));
            logger.info("【定时任务执行时长】{}：{}", target.getName(), crontabLog.getElapsedTime());
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
    private void doBefore(Crontab crontab, CrontabLog crontabLog, long startTime) {
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
    private void doAfter(CrontabLog crontabLog, Object result) {
        //默认调用成功
        crontabLog.setStatus(true);
        //设置任务返回结果
        if (result != null) {
            if (result instanceof ApiResult) {
                ApiResult apiResult = (ApiResult) result;
                crontabLog.setStatus(apiResult.getFlag());
                crontabLog.setResult(apiResult.getMsg());
                Object data = apiResult.getData();
                if (apiResult.getFlag() && data != null) {
                    crontabLog.setResult(data instanceof String ? data.toString() : JSON.toJSONString(data));
                }
            } else {
                crontabLog.setResult(result instanceof String ? result.toString() : JSON.toJSONString(result));
            }
        }
    }

    /**
     * 发送微信报警
     *
     * @author kuangq
     * @date 2022-12-23 10:39
     */
    private void sendWeChatAlarm(String name, String message) {
        WeChatAlarm weChatAlarm = new WeChatAlarm();
        weChatAlarm.setTitle(name);
        weChatAlarm.setContent(message);
        weChatAlarm.setDate(DateUtil.getTimestamp(System.currentTimeMillis(), DatePattern.YMD_HMS_1));
        SYSTEM_SERVER.sendWeChatAlarm(weChatAlarm);
    }
}