package com.loyer.common.datasource.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 数据源
 *
 * @author kuangq
 * @date 2021-06-25 16:29
 */
@Data
@NoArgsConstructor
public class DataSourceConfig {

    private DruidConfig druid;

    private List<DataSource> list;

    @Data
    @NoArgsConstructor
    public static class DataSource {

        //bean名称
        private String name;

        //是否为主数据源
        private boolean master;

        //驱动名
        private String driverClassName;

        //链接地址
        private String url;

        //账户名
        private String username;

        //密码
        private String password;
    }

    @Data
    @NoArgsConstructor
    public static class DruidConfig {

        //初始连接数
        private int initialSize;

        //最小连接池数量
        private int minIdle;

        //最大连接池数量
        private int maxActive;

        //获取连接等待超时的时间，单位毫秒
        private int maxWait;

        //检查空闲连接的频率，单位毫秒
        private int timeBetweenEvictionRunsMillis;

        //一个连接在池中最小生存的时间，单位毫秒
        private int minEvictableIdleTimeMillis;

        //一个连接在池中最大生存的时间，单位毫秒
        private int maxEvictableIdleTimeMillis;

        //数据源连接失败后是否跳出循环，设置true关闭自动重试
        private boolean breakAfterAcquireFailure;

        //数据源连接失败后的重试次数，设置0关闭自动重试，关闭后timeBetweenConnectErrorMillis失效
        private int connectionErrorRetryAttempts;

        //连接失败后第二次重试的间隔时间，单位毫秒，关闭后timeBetweenConnectErrorMillis失效
        private int timeBetweenConnectErrorMillis;

        //用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。如果validationQuery为null，testWhileIdle、testOnBorrow、testOnReturn都不会起作用
        private String validationQuery;

        //建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效
        private boolean testWhileIdle;

        //申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        private boolean testOnBorrow;

        //归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        private boolean testOnReturn;
    }
}