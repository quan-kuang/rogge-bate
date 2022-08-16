package com.loyer.common.datasource.inherit;

import com.loyer.common.datasource.handler.DynamicDataSourceHandler;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author kuangq
 * @title DynamicDataSource
 * @description 基于AbstractRoutingDataSource实现动态数据源装配
 * @date 2020-07-17 9:10
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * @param masterDataSource
     * @param slaveDataSources
     * @return
     * @author kuangq
     * @description 装配数据源
     * @date 2020-07-17 10:11
     */
    public DynamicDataSource(DataSource masterDataSource, Map<Object, Object> slaveDataSources) {
        super.setDefaultTargetDataSource(masterDataSource);
        super.setTargetDataSources(slaveDataSources);
        super.afterPropertiesSet();
    }

    /**
     * @param
     * @return java.lang.Object
     * @author kuangq
     * @description 数据源获取
     * @date 2020-07-17 10:10
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceHandler.getDataSource();
    }
}