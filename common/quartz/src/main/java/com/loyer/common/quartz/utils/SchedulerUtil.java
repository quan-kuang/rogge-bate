package com.loyer.common.quartz.utils;

import com.loyer.common.quartz.constant.CrontabConst;
import com.loyer.common.quartz.entity.Crontab;
import com.loyer.common.quartz.inherit.BanConcurrentExecute;
import com.loyer.common.quartz.inherit.ConcurrentExecute;
import lombok.SneakyThrows;
import org.quartz.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author kuangq
 * @title SchedulerUtil
 * @description 调度任务的工具类
 * @date 2020-12-16 23:15
 */
@Component
public class SchedulerUtil {

    @Resource
    private Scheduler scheduler;

    /**
     * @param crontab
     * @return void
     * @author kuangq
     * @description 创建调度任务
     * @date 2020-12-17 15:27
     */
    @SneakyThrows
    public void createScheduleJob(Crontab crontab) {
        //获取JobKey和triggerKey，将插入quartz相关表
        JobKey jobKey = getJobKey(crontab);
        TriggerKey triggerKey = getTriggerKey(crontab);
        //获取执行任务的类类型
        Class<? extends Job> jobClass = getQuartzJobClass(crontab.getConcurrent());
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobKey).build();
        //创建表达式调度构建器
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(crontab.getExpression());
        cronScheduleBuilder = handleCronScheduleBuilder(crontab, cronScheduleBuilder);
        //依据cron表达式构建CronTrigger
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(cronScheduleBuilder).build();
        //放入参数，运行时的方法根据key值获取crontab的属性
        jobDetail.getJobDataMap().put(CrontabConst.TASK_PROPERTIES_KEY, crontab);
        //判断预创建的任务是否存在
        if (scheduler.checkExists(jobKey)) {
            //防止创建异常，先删除，然后在执行创建操作
            scheduler.deleteJob(jobKey);
        }
        //创建任务
        scheduler.scheduleJob(jobDetail, cronTrigger);
        //暂停任务
        if (!crontab.getStatus()) {
            scheduler.pauseJob(jobKey);
        }
    }

    /**
     * @param crontab
     * @return void
     * @author kuangq
     * @description 执行一次定时任务
     * @date 2020-12-17 15:45
     */
    @SneakyThrows
    public void executeCrontab(Crontab crontab) {
        JobKey jobKey = getJobKey(crontab);
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(CrontabConst.TASK_PROPERTIES_KEY, crontab);
        scheduler.triggerJob(jobKey, dataMap);
    }

    /**
     * @param crontab
     * @return void
     * @author kuangq
     * @description 删除调度任务
     * @date 2020-12-17 15:53
     */
    @SneakyThrows
    public void deleteCrontab(Crontab crontab) {
        scheduler.deleteJob(getJobKey(crontab));
    }

    /**
     * @param
     * @return void
     * @author kuangq
     * @description 清空调度任务
     * @date 2020-12-17 17:39
     */
    @SneakyThrows
    public void clear() {
        scheduler.clear();
    }

    /**
     * @param concurrent
     * @return java.lang.Class<? extends org.quartz.Job>
     * @author kuangq
     * @description 获取定时任务执行类
     * @date 2020-12-17 15:25
     */
    private static Class<? extends Job> getQuartzJobClass(boolean concurrent) {
        return concurrent ? ConcurrentExecute.class : BanConcurrentExecute.class;
    }

    /**
     * @param crontab
     * @return org.quartz.TriggerKey
     * @author kuangq
     * @description 创建触发器的KEY
     * @date 2020-12-17 15:25
     */
    public static TriggerKey getTriggerKey(Crontab crontab) {
        return TriggerKey.triggerKey(CrontabConst.TASK_NAME_PREFIX + crontab.getUuid(), crontab.getType());
    }

    /**
     * @param crontab
     * @return org.quartz.JobKey
     * @author kuangq
     * @description 创建任务对象的KEY
     * @date 2020-12-17 15:26
     */
    public static JobKey getJobKey(Crontab crontab) {
        return JobKey.jobKey(CrontabConst.TASK_NAME_PREFIX + crontab.getUuid(), crontab.getType());
    }

    /**
     * @param crontab
     * @param cronScheduleBuilder
     * @return org.quartz.CronScheduleBuilder
     * @author kuangq
     * @description 设置定时任务策略（此处只设置默认或非立即执行）
     * @date 2020-12-17 15:31
     */
    public static CronScheduleBuilder handleCronScheduleBuilder(Crontab crontab, CronScheduleBuilder cronScheduleBuilder) {
        return crontab.getStatus() ? cronScheduleBuilder : cronScheduleBuilder.withMisfireHandlingInstructionDoNothing();
    }
}