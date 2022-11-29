package com.loyer.modules.system.service.impl;

import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.common.datasource.annotation.DataSourceAnnotation;
import com.loyer.common.datasource.enums.DataSourceType;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.utils.GeneralUtil;
import com.loyer.modules.system.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 示例ServiceImpl
 *
 * @author kuangq
 * @date 2019-08-01 10:59
 */
@Service
public class DemoServiceImpl implements DemoService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private com.loyer.modules.system.mapper.postgresql.DemoMapper postgresqlDemoMapper;

    @Resource
    private com.loyer.modules.system.mapper.mysql.DemoMapper mysqlDemoMapper;

    @Resource
    private com.loyer.modules.system.mapper.oracle.DemoMapper oracleDemoMapper;

    /**
     * 查询数据库基本信息
     *
     * @author kuangq
     * @date 2019-11-15 16:55
     */
    @Override
    public ApiResult queryDataBase() {
        //从上下文中获取bean做内部调用，解决内部方法调用AOP无效的问题
        DemoServiceImpl demoServiceImpl = ContextUtil.getBean(this.getClass());
        Map<String, Object> resultMap = new HashMap<String, Object>(3) {{
            String uuid = GeneralUtil.getUuid();
            put("postgresql", getDataSource("postgresqlDataSource") ? demoServiceImpl.queryDataBase1(uuid) : "链接失败");
            put("mysql", getDataSource("mysqlDataSource") ? demoServiceImpl.queryDataBase2(uuid) : "链接失败");
            put("oracle", getDataSource("oracleDataSource") ? demoServiceImpl.queryDataBase3(uuid) : "链接失败");
        }};
        return ApiResult.success(resultMap);
    }

    /**
     * 查询POSTGRESQL数据库
     *
     * @author kuangq
     * @date 2020-07-16 17:47
     */
    @Transactional(transactionManager = "dataSourceTransactionManager", rollbackFor = Exception.class)
    @DataSourceAnnotation(DataSourceType.POSTGRESQL)
    public Object queryDataBase1(String uuid) {
        postgresqlDemoMapper.insertTest(uuid);
        return postgresqlDemoMapper.queryDataBase();
    }

    /**
     * 查询MYSQL数据库
     *
     * @author kuangq
     * @date 2020-07-16 17:47
     */
    @Transactional(transactionManager = "dataSourceTransactionManager", rollbackFor = Exception.class)
    @DataSourceAnnotation(DataSourceType.MYSQL)
    public Object queryDataBase2(String uuid) {
        mysqlDemoMapper.insertTest(uuid);
        return mysqlDemoMapper.queryDataBase();
    }

    /**
     * 查询ORCLE数据库
     *
     * @author kuangq
     * @date 2020-07-16 17:47
     */
    @Transactional(transactionManager = "dataSourceTransactionManager", rollbackFor = Exception.class)
    @DataSourceAnnotation(DataSourceType.ORACLE)
    public Object queryDataBase3(String uuid) {
        oracleDemoMapper.insertTest(uuid);
        return oracleDemoMapper.queryDataBase();
    }

    private boolean getDataSource(String beanName) {
        @SuppressWarnings("AlibabaThreadPoolCreation")
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<Boolean> futureTask = new FutureTask<>(() -> {
            try {
                DataSource dataSource = (DataSource) ContextUtil.getApplicationContext().getBean(beanName);
                dataSource.getConnection();
                return true;
            } catch (Exception e) {
                logger.error("【数据源链接失败】{}：{}", beanName, e.getMessage());
                return false;
            }
        });
        executor.execute(futureTask);
        try {
            return futureTask.get(3000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("【数据源链接任务执行失败】{}：{}", beanName, e.getMessage());
            return false;
        } finally {
            executor.shutdown();
        }
    }
}