package com.loyer.common.core.utils.common;

import java.util.concurrent.*;

/**
 * @author kuangq
 * @title ExecutorUtil
 * @description 线程执行工具类
 * @date 2021-09-16 13:55
 */
public class ExecutorUtil {

    /**
     * @param
     * @return java.util.concurrent.ExecutorService
     * @author kuangq
     * @description 获取线程池
     * @date 2021-09-16 13:57
     */
    public static ExecutorService getExecutorService() {
        //核心线程数
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        //最大线程数
        int maximumPoolSize = 2 * corePoolSize + 1;
        //空闲等待时间
        long keepAliveTime = 3L;
        //等待时间单位
        TimeUnit timeUnit = TimeUnit.SECONDS;
        //等待队列
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(10);
        //线程工厂
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        //拒绝策略
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        //创建线程池
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, workQueue, threadFactory, handler);
    }
}