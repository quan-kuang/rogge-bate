package com.loyer.common.datasource.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.loyer.common.core.utils.reflect.EntityUtil;
import com.loyer.common.datasource.config.JpaDatabaseConfig;
import com.loyer.common.datasource.entity.PageParams;
import com.loyer.common.datasource.entity.PageResult;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

/**
 * JPA数据库操作工具类
 *
 * @author kuangq
 * @date 2019-11-15 12:47
 */
@ConditionalOnBean(JpaDatabaseConfig.class)
@Component
public class EntityManagerUtil {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Qualifier("jpaEntityManager")
    @Resource
    private EntityManager dql;

    @Qualifier("jpaEntityManager")
    @PersistenceContext
    private EntityManager dml;

    /**
     * 执行DQL语句操作
     *
     * @author kuangq
     * @date 2021-11-30 13:09
     */
    @Transactional(transactionManager = "jpaTransactionManager", readOnly = true)
    public List<?> dqlHandle(String sql) {
        try {
            Query query = dql.createNativeQuery(sql);
            query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            return query.getResultList();
        } catch (Exception e) {
            logger.error("dqlHandle error：{}", sql);
            throw e;
        }
    }

    /**
     * 执行DQL语句操作
     *
     * @author kuangq
     * @date 2022-07-20 10:02
     */
    @Transactional(transactionManager = "jpaTransactionManager", readOnly = true)
    public <T> List<T> dqlHandle(String sql, Class<T> clazz) {
        return JSONArray.parseArray(JSON.toJSONString(dqlHandle(sql)), clazz);
    }

    /**
     * 执行DML语句操作
     *
     * @author kuangq
     * @date 2019-11-15 13:12
     */
    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class)
    public int dmlHandle(String sql) {
        Query query = dml.createNativeQuery(sql);
        return query.executeUpdate();
    }

    /**
     * 执行DML语句操作
     *
     * @author kuangq
     * @date 2022-07-29 14:47
     */
    @Transactional(transactionManager = "jpaTransactionManager", rollbackFor = Exception.class)
    public int dmlHandle(String sql, Object entity) {
        try {
            Query query = dml.createNativeQuery(sql);
            Map<String, Object> map = EntityUtil.entityToMap(entity);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (sql.contains(String.format(":%s", entry.getKey()))) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
            return query.executeUpdate();
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            logger.error("dmlHandle error：{}", sql);
            throw e;
        }
    }

    /**
     * 分页查询
     *
     * @author kuangq
     * @date 2022-07-29 14:59
     */
    public <T> PageResult<T> queryPageInfo(String sql, PageParams pageParams) {
        //查询count
        String countSql = String.format("SELECT COUNT(*) FROM (%s) temp", sql);
        int total = Integer.parseInt(((Map<?, ?>) dqlHandle(countSql).get(0)).get("count").toString());
        //查询data
        String dataSql = String.format("SELECT * FROM (%s) temp limit %s offset %s * (%s - 1)", sql, pageParams.getPageSize(), pageParams.getPageSize(), pageParams.getPageNum());
        List list = dqlHandle(dataSql, pageParams.getAClass());
        //返回分页信息
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setTotal(total);
        pageResult.setList(list);
        return pageResult;
    }
}