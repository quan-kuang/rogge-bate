package com.loyer.common.datasource.inherit;

import com.loyer.common.datasource.handler.DynamicDataSourceHandler;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 基于AbstractRoutingDataSource实现动态数据源装配
 *
 * @author kuangq
 * @date 2020-07-17 9:10
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 装配数据源
     *
     * @author kuangq
     * @date 2020-07-17 10:11
     */
    public DynamicDataSource(DataSource masterDataSource, Map<Object, Object> slaveDataSources) {
        super.setDefaultTargetDataSource(masterDataSource);
        super.setTargetDataSources(slaveDataSources);
        super.afterPropertiesSet();
    }

    /**
     * 数据源获取
     *
     * @author kuangq
     * @date 2020-07-17 10:10
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DynamicDataSourceHandler.getDataSource();
    }
}