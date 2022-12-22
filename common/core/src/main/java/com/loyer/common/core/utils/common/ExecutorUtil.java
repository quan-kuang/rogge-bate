package com.loyer.common.core.utils.common;

import java.util.concurrent.*;

/**
 * 线程执行工具类
 *
 * @author kuangq
 * @date 2021-09-16 13:55
 */
public class ExecutorUtil {

    private static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = getThreadPoolExecutor();

    /**
     * 获取线程池
     *
     * @author kuangq
     * @date 2021-09-16 13:57
     */
    public static ThreadPoolExecutor getThreadPoolExecutor() {
        //核心线程数
        int corePoolSize = Runtime.getRuntime().availableProcessors();
        //最大线程数
        int maximumPoolSize = 3 * corePoolSize + 1;
        //空闲等待时间
        long keepAliveTime = 3L;
        //等待时间单位
        TimeUnit timeUnit = TimeUnit.SECONDS;
        //等待队列
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(1000);
        //线程工厂
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        //拒绝策略
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
        //创建线程池
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, workQueue, threadFactory, handler);
    }

    /**
     * 异步执行任务
     *
     * @author kuangq
     * @date 2022-08-18 18:08
     */
    public static void task(Runnable runnable) {
        THREAD_POOL_EXECUTOR.execute(runnable);
    }
}