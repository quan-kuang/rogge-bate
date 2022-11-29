package com.loyer.common.datasource.utils;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * 创建数据源工具类
 *
 * @author kuangq
 * @date 2020-05-26 9:25
 */
public class BuildDatabaseUtil {

    /**
     * 加载mapper包
     *
     * @author kuangq
     * @date 2020-05-26 21:52
     */
    public static MapperScannerConfigurer buildMapperScanner(String basePackage, String beanName) {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setBasePackage(basePackage);
        mapperScannerConfigurer.setSqlSessionFactoryBeanName(beanName);
        return mapperScannerConfigurer;
    }

    /**
     * 加载xml
     *
     * @author kuangq
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
     * 配置事务管理器
     *
     * @author kuangq
     * @date 2020-05-26 21:52
     */
    public static DataSourceTransactionManager buildTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}