package com.loyer.common.datasource.handler;

/**
 * @author kuangq
 * @title DynamicDataSourceHandler
 * @description 动态数据源处理器
 * @date 2019-11-20 17:50
 */
public class DynamicDataSourceHandler {

    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本
     */
    private static final ThreadLocal<String> CONTEXT_HANDLE = new ThreadLocal<>();

    /**
     * @param dbType
     * @return void
     * @author kuangq
     * @description 设置数据源
     * @date 2020-07-16 16:08
     */
    public static void setDataSource(String dbType) {
        CONTEXT_HANDLE.set(dbType);
    }

    /**
     * @param
     * @return java.lang.String
     * @author kuangq
     * @description 获取数据源
     * @date 2020-07-16 16:10
     */
    public static String getDataSource() {
        return CONTEXT_HANDLE.get();
    }

    /**
     * @param
     * @return void
     * @author kuangq
     * @description 清除数据源
     * @date 2020-07-16 16:10
     */
    public static void removeDataSource() {
        CONTEXT_HANDLE.remove();
    }
}