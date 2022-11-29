package com.loyer.common.datasource.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

/**
 * JPA链接数据库配置
 *
 * @author kuangq
 * @date 2019-11-15 12:28
 */
@ConditionalOnProperty(prefix = "spring.jpa", name = "enable", havingValue = "true")
@Component
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "jpaEntityManagerFactory", //配置连接工厂
        transactionManagerRef = "jpaTransactionManager", //配置事物管理器
        basePackages = {"com.loyer.**.**.mapper.repository"}) //设置Repository所在位置
public class JpaDatabaseConfig {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    //装配数据库方言
    @Value("${spring.jpa.hibernate.pgsql-dialect}")
    String dialect;
    //装配JPA配置
    @Resource
    private JpaProperties jpaProperties;

    //装配数据源
    @Bean(name = "jpaDataSource")
    @ConfigurationProperties(prefix = "spring.jpa")
    public DataSource dataSource() {
        logger.info("【注册数据源】：jpaDataSource");
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 配置EntityManager连接器
     *
     * @author kuangq
     * @date 2019-11-15 10:47
     */
    @Bean(name = "jpaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean(@Qualifier("jpaDataSource") DataSource dataSource, EntityManagerFactoryBuilder entityManagerFactoryBuilder) {
        Map<String, String> properties = jpaProperties.getProperties();
        //设置数据库方言
        properties.put("hibernate.dialect", dialect);
        //自定义命令规则
        properties.put("hibernate.physical_naming_strategy", CustomPhysicalNamingStrategy.class.getName());
        //创建链接
        return entityManagerFactoryBuilder
                //设置数据源
                .dataSource(dataSource)
                //设置数据源属性
                .properties(properties)
                //设置实体类所在位置、扫描所有带有@Entity 注解的类
                .packages("com.loyer.**.**.entity")
                //将EntityManagerFactory注入到Repository之中、通过EntityManager执行数据库操作
                .persistenceUnit("persistenceUnit")
                .build();
    }

    /**
     * 配置连接器
     *
     * @author kuangq
     * @date 2019-11-15 12:21
     */
    @Bean(name = "jpaEntityManager")
    public EntityManager entityManager(@Qualifier("jpaEntityManagerFactory") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
        return Objects.requireNonNull(localContainerEntityManagerFactoryBean.getObject()).createEntityManager();
    }

    /**
     * 配置事务管理器
     *
     * @author kuangq
     * @date 2019-11-15 10:47
     */
    @Bean(name = "jpaTransactionManager")
    public PlatformTransactionManager platformTransactionManager(@Qualifier("jpaEntityManagerFactory") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
        return new JpaTransactionManager(Objects.requireNonNull(localContainerEntityManagerFactoryBean.getObject()));
    }
}