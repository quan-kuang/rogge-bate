package com.loyer.common.datasource.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.loyer.common.core.utils.reflect.BeanUtil;
import com.loyer.common.datasource.entity.DataSourceConfig;
import com.loyer.common.datasource.enums.DataSourceType;
import com.loyer.common.datasource.inherit.DynamicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源配置
 *
 * @author kuangq
 * @date 2019-11-20 17:48
 */
@Component
public class DynamicDataSourceConfig implements EnvironmentAware, BeanDefinitionRegistryPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //TODO 注册BEAN对象
    }

    /**
     * 根据配置文件动态注册数据源
     *
     * @author kuangq
     * @date 2021-06-27 12:17
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //获取配置文件配置，并绑定
        BindResult<DataSourceConfig> dataSourceConfigBindResult = Binder.get(environment).bind("spring.datasource", DataSourceConfig.class);
        DataSourceConfig dataSourceConfig = dataSourceConfigBindResult.get();
        DataSourceConfig.DruidConfig druidConfig = dataSourceConfig.getDruid();
        //创建主从数据源
        DataSource masterDataSource = null;
        // <String, DataSource>
        Map<Object, Object> slaveDataSources = new HashMap<>(dataSourceConfig.getList().size());
        //遍历需要创建的数据源
        for (DataSourceConfig.DataSource dataSource : dataSourceConfig.getList()) {
            String beanName = dataSource.getName();
            //获取DruidDataSource
            DruidDataSource druidDataSource = getDruidDataSource(druidConfig, dataSource);
            //设置主数据源
            if (dataSource.isMaster() && masterDataSource == null) {
                masterDataSource = druidDataSource;
            }
            //设置从数据源
            else {
                slaveDataSources.put(beanName.toUpperCase(), druidDataSource);
            }
            String dataSourceName = beanName + "DataSource";
            logger.info("【注册数据源】：{}", dataSourceName);
            //注册DataSource
            BeanDefinition dataSourceBeanDefinition = getBeanDefinition(DataSource.class, druidDataSource);
            registry.registerBeanDefinition(dataSourceName, dataSourceBeanDefinition);
        }
        //注册动态数据源
        DynamicDataSource dynamicDataSource = new DynamicDataSource(masterDataSource, slaveDataSources);
        BeanDefinition dynamic = getBeanDefinition(DynamicDataSource.class, dynamicDataSource);
        dynamic.setPrimary(true);
        registry.registerBeanDefinition("dynamicDataSource", dynamic);
    }

    /**
     * 设置Druid相关属性
     *
     * @author kuangq
     * @date 2021-06-27 12:15
     */
    private DruidDataSource getDruidDataSource(DataSourceConfig.DruidConfig druidConfig, DataSourceConfig.DataSource dataSource) {
        DruidDataSource druidDataSource = new DruidDataSource();
        //设置druidConfig属性
        BeanUtil.copyBean(druidConfig, druidDataSource);
        druidDataSource.setUrl(dataSource.getUrl());
        druidDataSource.setUsername(dataSource.getUsername());
        druidDataSource.setPassword(dataSource.getPassword());
        druidDataSource.setDriverClassName(dataSource.getDriverClassName());
        //如果驱动名包含oracle，检测SQL做额外处理
        if (StringUtils.containsIgnoreCase(dataSource.getDriverClassName(), DataSourceType.ORACLE.name().toLowerCase())) {
            druidDataSource.setValidationQuery("SELECT 1 FROM DUAL");
        }
        return druidDataSource;
    }

    /**
     * 获取bean
     *
     * @author kuangq
     * @date 2021-06-27 12:16
     */
    private BeanDefinition getBeanDefinition(Class clazz, Object instance) {
        return BeanDefinitionBuilder.genericBeanDefinition(clazz, () -> instance).getRawBeanDefinition();
    }
}