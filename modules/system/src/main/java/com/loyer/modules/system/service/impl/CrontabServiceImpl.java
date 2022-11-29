package com.loyer.modules.system.service.impl;

import com.loyer.common.core.enums.DatePattern;
import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.datasource.entity.PageResult;
import com.loyer.common.datasource.entity.SearchSourceQuery;
import com.loyer.common.datasource.utils.ElasticsearchUtil;
import com.loyer.common.datasource.utils.PageHelperUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.quartz.entity.Crontab;
import com.loyer.common.quartz.entity.CrontabLog;
import com.loyer.common.quartz.utils.CronUtil;
import com.loyer.common.quartz.utils.InvokeUtil;
import com.loyer.common.quartz.utils.SchedulerUtil;
import com.loyer.common.security.annotation.PermissionAnnotation;
import com.loyer.modules.system.mapper.postgresql.CrontabMapper;
import com.loyer.modules.system.service.CrontabService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * 定时任务ServiceImpl
 *
 * @author kuangq
 * @date 2020-12-16 21:58
 */
@Service
public class CrontabServiceImpl implements CrontabService {

    @Resource
    private CrontabMapper crontabMapper;

    @Resource
    private SchedulerUtil schedulerUtil;

    /**
     * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
     *
     * @author kuangq
     * @date 2020-12-17 17:34
     */
    @PostConstruct
    public void init() {
        schedulerUtil.clear();
        List<Crontab> crontabList = crontabMapper.selectCrontab(null);
        for (Crontab crontab : crontabList) {
            schedulerUtil.createScheduleJob(crontab);
        }
    }

    /**
     * 保存定时任务信息
     *
     * @author kuangq
     * @date 2020-12-16 21:58
     */
    @Override
    @PermissionAnnotation
    public ApiResult saveCrontab(Crontab crontab) {
        //校验目标字符串的合法性
        InvokeUtil.invokeMethod(crontab.getInvokeTarget(), false);
        //保存至数据库
        if (crontabMapper.saveCrontab(crontab) > 0) {
            //新增定时任务
            schedulerUtil.createScheduleJob(crontab);
        }
        return ApiResult.success();
    }

    /**
     * 查询定时任务信息
     *
     * @author kuangq
     * @date 2020-12-16 21:58
     */
    @Override
    public ApiResult selectCrontab(Crontab crontab) {
        //判断是否为分页查询
        if (PageHelperUtil.isPaging(crontab)) {
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            PageResult<Crontab> pageResult = PageHelperUtil.pagingQuery(crontabMapper, methodName, crontab);
            pageResult.getList().forEach(item -> item.setExecuteTime(CronUtil.getExecuteTime(item.getExpression(), 1).get(0)));
            return ApiResult.success(pageResult);
        } else {
            List<Crontab> crontabList = crontabMapper.selectCrontab(crontab);
            crontabList.forEach(item -> item.setExecuteTime(CronUtil.getExecuteTime(item.getExpression(), 1).get(0)));
            return ApiResult.success(crontabList);
        }
    }

    /**
     * 获取表达式执行时间
     *
     * @author kuangq
     * @date 2021-09-18 11:31
     */
    @Override
    public ApiResult getExecuteTime(String expression, Integer count) {
        return ApiResult.success(CronUtil.getExecuteTime(expression, count));
    }

    /**
     * 删除定时任务信息
     *
     * @author kuangq
     * @date 2020-12-16 21:58
     */
    @Override
    public ApiResult deleteCrontab(String... uuids) {
        //查询任务信息
        List<Crontab> crontabList = crontabMapper.selectCrontabType(uuids);
        //先从数据库删除
        if (crontabMapper.deleteCrontab(uuids) > 0) {
            //再从任务中删除
            for (Crontab crontab : crontabList) {
                schedulerUtil.deleteCrontab(crontab);
            }
        }
        return ApiResult.success(crontabList.size());
    }

    /**
     * 校验corn表达式的有效性
     *
     * @author kuangq
     * @date 2020-12-17 12:31
     */
    @Override
    public ApiResult checkCornExpression(String cornExpression) {
        if (CronUtil.isValid(cornExpression)) {
            return ApiResult.success();
        }
        return ApiResult.hintEnum(HintEnum.HINT_1091, CronUtil.getInvalidMessage(cornExpression));
    }

    /**
     * 立即执行一次定时任务
     *
     * @author kuangq
     * @date 2020-12-17 13:46
     */
    @Override
    public ApiResult executeCrontab(Crontab crontab) {
        schedulerUtil.executeCrontab(crontab);
        return ApiResult.success();
    }

    /**
     * 查询定时任务日志
     *
     * @author kuangq
     * @date 2020-12-17 23:09
     */
    @Override
    public ApiResult selectCrontabLog(CrontabLog crontabLog) {
        SearchSourceQuery searchSourceQuery = new SearchSourceQuery();
        //设置查询全部数据
        searchSourceQuery.setHitAll(true);
        //设置查询索引，对应数据库表名，禁止大写
        searchSourceQuery.setIndex("crontab_log");
        //设置查询字段名称，为空查询全部字段
        searchSourceQuery.setFields("");
        //设置排序字段
        searchSourceQuery.setSortField("beginTime");
        //设置分页数
        searchSourceQuery.setPageNum(crontabLog.getPageNum());
        //设置分页大小
        searchSourceQuery.setPageSize(crontabLog.getPageSize());
        //设置查询条件
        BoolQueryBuilder boolQueryBuilder = boolQuery();
        //设置查询操作状态
        if (crontabLog.getStatus() != null) {
            boolQueryBuilder.must(termQuery("status", crontabLog.getStatus()));
        }
        //设置查询任务类型
        if (StringUtils.isNotBlank(crontabLog.getCrontabType())) {
            boolQueryBuilder.must(termQuery("crontabType", crontabLog.getCrontabType()));
        }
        //设置任务名称的模糊查询条件
        if (StringUtils.isNotBlank(crontabLog.getCrontabName())) {
            boolQueryBuilder.must(boolQuery().should(wildcardQuery("crontabName.keyword", "*" + crontabLog.getCrontabName() + "*")));
        }
        //创建范围条件
        RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("beginTime");
        //开始时间不为空设置大于等于条件
        if (StringUtils.isNotBlank(crontabLog.getStartTime())) {
            rangeQueryBuilder.gte(DateUtil.getTimestamp(crontabLog.getStartTime(), DatePattern.YMD_HMS_1));
        }
        //结束时间不为空设置小于等于条件
        if (StringUtils.isNotBlank(crontabLog.getEndTime())) {
            rangeQueryBuilder.lte(DateUtil.getTimestamp(crontabLog.getEndTime(), DatePattern.YMD_HMS_1));
        }
        //设置查询时间范围
        boolQueryBuilder.must(rangeQueryBuilder);
        //设置上述查询条件
        searchSourceQuery.setQueryBuilder(boolQueryBuilder);
        //设置查询类型
        if (crontabLog.getParams().containsKey("mode")) {
            searchSourceQuery.setMode(crontabLog.getParams().get("mode").toString());
        }
        //es查询数据
        return ApiResult.success(ElasticsearchUtil.selectData(searchSourceQuery));
    }

    /**
     * 删除定时任务日志
     *
     * @author kuangq
     * @date 2020-12-17 23:09
     */
    @Override
    public ApiResult deleteCrontabLog(String... uuids) {
        ElasticsearchUtil.deleteData("crontab_log", uuids);
        return ApiResult.success();
    }

    /**
     * 保存定时任务日志
     *
     * @author kuangq
     * @date 2021-03-07 18:51
     */
    @Override
    public ApiResult insertCrontabLog(CrontabLog crontabLog) {
        ElasticsearchUtil.insertData(ElasticsearchUtil.getIndex(crontabLog), crontabLog.getUuid(), crontabLog);
        return ApiResult.success();
    }
}