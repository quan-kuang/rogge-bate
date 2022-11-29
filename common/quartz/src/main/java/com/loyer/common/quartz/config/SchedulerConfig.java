package com.loyer.common.quartz.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 定时任务配置
 *
 * @author kuangq
 * @date 2020-12-16 14:50
 */
@Configuration
public class SchedulerConfig {

    /**
     * 创建调度器的工厂Bean
     *
     * @author kuangq
     * @date 2020-12-16 17:02
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(@Qualifier("mysqlDataSource") DataSource dataSource) {
        //调度器配置
        Properties properties = new Properties();
        //调度器实例编号自动生成
        properties.put("org.quartz.scheduler.instanceId", "AUTO");
        //调度器实例名称
        properties.put("org.quartz.scheduler.instanceName", "RoggeBateScheduler");
        //最大能忍受的触发超时时间（单位毫秒）
        properties.put("org.quartz.jobStore.misfireThreshold", "10000");
        //JobStoreTX（自己管理事务）和JobStoreCMT（application server管理事务，即全局事务JTA）
        properties.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        //JDBC驱动类型，此处设置为MYSQL，PG数据库的为：PostgreSQLDelegate
        properties.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        //相关数据表前缀名
        properties.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        //线程池实现类
        properties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        //执行最大并发线程数量
        properties.put("org.quartz.threadPool.threadCount", "5");
        //线程优先级
        properties.put("org.quartz.threadPool.threadPriority", "5");
        //配置是否启动自动加载数据库内的定时任务，默认true
        properties.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
        //是否集群、负载均衡、容错，如果应用在集群中设置为false会出错
        properties.put("org.quartz.jobStore.isClustered", "true");
        //加锁，若影响性能可在handle实现类中加上分布式锁
        properties.put("org.quartz.jobStore.acquireTriggersWithinLock", "true");
        //分布式节点有效性检查时间间隔，单位：毫秒
        properties.put("org.quartz.jobStore.clusterCheckinInterval", "10000");
        //jobStore处理未按时触发的Job的数量
        properties.put("org.quartz.jobStore.maxMisfiresToHandleAtATime", "1");
        //true则调用connection的setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE) 方法
        properties.put("org.quartz.jobStore.txIsolationLevelSerializable", "true");
        //创建FactoryBean
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        //装配属性设置
        schedulerFactoryBean.setQuartzProperties(properties);
        //设置调度任务上下文的KEY
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        //设置自动启动，默认为true
        schedulerFactoryBean.setAutoStartup(true);
        //延时启动，应用启动成功后在启动
        schedulerFactoryBean.setStartupDelay(10);
        //启动时更新己存在的JOB，避免每次修改targetObject后删除qrtz_job_details表对应记录
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        //设置数据源
        schedulerFactoryBean.setDataSource(dataSource);
        return schedulerFactoryBean;
    }
}