package com.loyer.common.datasource.config;

import com.loyer.common.datasource.inherit.DynamicDataSource;
import com.loyer.common.datasource.utils.BuildDatabaseUtil;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * MyBatis配置
 *
 * @author kuangq
 * @date 2020-07-16 22:23
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
@EnableAspectJAutoProxy(exposeProxy = true) //通过aop框架暴露该代理对象，使其能通过上下文访问，处理内部方法调用切面无效问题
public class MyBatisConfig {

    //xml文件路径
    @SuppressWarnings("FieldCanBeLocal")
    private static final String MAPPER_LOCATIONS = "classpath:mapper/**/*.xml";

    //mapper所在路径
    @SuppressWarnings("FieldCanBeLocal")
    private static final String BASE_PACKAGE = "com.loyer.**.**.mapper.**";

    //pojo类所在路径
    @SuppressWarnings("FieldCanBeLocal")
    private static final String TYPE_ALIASES_PACKAGE = "com.loyer.**.**.entity";

    /**
     * 扫描xml文件
     *
     * @author kuangq
     * @date 2020-07-17 11:31
     */
    @Bean
    public static SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DynamicDataSource dynamicDataSource) {
        return BuildDatabaseUtil.buildSqlSessionFactory(dynamicDataSource, TYPE_ALIASES_PACKAGE, MAPPER_LOCATIONS);
    }

    /**
     * 扫描mapper包
     *
     * @author kuangq
     * @date 2020-07-17 11:30
     */
    @Bean
    public static MapperScannerConfigurer mapperScannerConfigurer() {
        return BuildDatabaseUtil.buildMapperScanner(BASE_PACKAGE, "sqlSessionFactory");
    }

    /**
     * 配置事务管理器
     *
     * @author kuangq
     * @date 2020-07-17 11:30
     */
    @Bean
    public static DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("dynamicDataSource") DynamicDataSource dynamicDataSource) {
        return BuildDatabaseUtil.buildTransactionManager(dynamicDataSource);
    }
}