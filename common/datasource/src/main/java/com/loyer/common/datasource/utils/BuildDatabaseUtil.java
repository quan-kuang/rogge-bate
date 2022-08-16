package com.loyer.common.datasource.utils;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author kuangq
 * @title BuildDatabaseUtil
 * @description 创建数据源工具类
 * @date 2020-05-26 9:25
 */
public class BuildDatabaseUtil {

    /**
     * @param basePackage
     * @param beanName
     * @return org.mybatis.spring.mapper.MapperScannerConfigurer
     * @author kuangq
     * @description 加载mapper包
     * @date 2020-05-26 21:52
     */
    public static MapperScannerConfigurer buildMapperScanner(String basePackage, String beanName) {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage(basePackage);
        mapperScannerConfigurer.setSqlSessionFactoryBeanName(beanName);
        return mapperScannerConfigurer;
    }

    /**
     * @param dataSource
     * @param mapperLocations
     * @return org.apache.ibatis.session.SqlSessionFactory
     * @author kuangq
     * @description 加载xml
     * @date 2020-05-26 21:52
     */
    public static SqlSessionFactory buildSqlSessionFactory(DataSource dataSource, String typeAliasesPackage, String mapperLocations) {
        try {
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            //加载数据源
            sqlSessionFactoryBean.setDataSource(dataSource);
            //加载pojo文件
            sqlSessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);
            //加载xml文件
            sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
            //创建mybatis.configuration对象
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            //设置_符号改驼峰式命名
            configuration.setMapUnderscoreToCamelCase(true);
            sqlSessionFactoryBean.setConfiguration(configuration);
            return sqlSessionFactoryBean.getObject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param dataSource
     * @return org.springframework.jdbc.datasource.DataSourceTransactionManager
     * @author kuangq
     * @description 配置事务管理器
     * @date 2020-05-26 21:52
     */
    public static DataSourceTransactionManager buildTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
