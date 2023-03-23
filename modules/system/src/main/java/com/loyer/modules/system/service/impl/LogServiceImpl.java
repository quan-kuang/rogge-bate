package com.loyer.modules.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loyer.common.core.entity.OperateLog;
import com.loyer.common.core.entity.User;
import com.loyer.common.core.enums.CalculateType;
import com.loyer.common.core.enums.DatePattern;
import com.loyer.common.core.utils.common.CalculateUtil;
import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.core.utils.common.ExecutorUtil;
import com.loyer.common.core.utils.reflect.BeanUtil;
import com.loyer.common.datasource.entity.PageResult;
import com.loyer.common.datasource.entity.SearchSourceQuery;
import com.loyer.common.datasource.utils.ElasticsearchUtil;
import com.loyer.common.datasource.utils.PageHelperUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.redis.constant.PrefixConst;
import com.loyer.common.redis.utils.CacheUtil;
import com.loyer.common.security.annotation.PermissionAnnotation;
import com.loyer.common.security.aspect.PermissionAspect;
import com.loyer.common.security.entity.LoginUser;
import com.loyer.common.security.enums.PermissionType;
import com.loyer.common.security.utils.SecurityUtil;
import com.loyer.modules.system.mapper.postgresql.LogMapper;
import com.loyer.modules.system.service.LogService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * 日志ServiceImpl
 *
 * @author kuangq
 * @date 2020-06-02 16:50
 */
@Service
public class LogServiceImpl implements LogService {

    @Resource
    private LogMapper logMapper;

    /**
     * 保存操作日志
     *
     * @author kuangq
     * @date 2020-06-09 9:44
     */
    @Override
    public ApiResult insertOperateLog(OperateLog operateLog) {
        //设置操作用户
        setUser(operateLog);
        //ES保存日志
        ElasticsearchUtil.insertData(ElasticsearchUtil.getIndex(operateLog), operateLog.getUuid(), operateLog);
        //数据库保存日志
        return ApiResult.success(logMapper.insertOperateLog(operateLog));
    }

    /**
     * 设置操作用户
     *
     * @author kuangq
     * @date 2020-10-16 10:22
     */
    private void setUser(OperateLog operateLog) {
        User user;
        try {
            //处理登录接口时保存用户信息
            final String method = "login";
            if (method.equals(operateLog.getMethod())) {
                ApiResult apiResult = JSONObject.parseObject(operateLog.getOutArgs(), ApiResult.class);
                LoginUser loginUser = JSONObject.parseObject(apiResult.getData().toString(), LoginUser.class);
                user = loginUser.getUser();
            } else {
                //根据token获取用户信息
                user = Objects.requireNonNull(SecurityUtil.getLoginUser(operateLog.getToken())).getUser();
            }
        } catch (Exception e) {
            user = new User();
        }
        operateLog.setCreatorId(user.getUuid());
        operateLog.setCreatorName(user.getName());
        operateLog.setCreateDeptId(user.getDeptId());
    }

    /**
     * 查询操作日志
     *
     * @author kuangq
     * @date 2020-06-09 9:44
     */
    @Override
    @PermissionAnnotation
    public ApiResult selectOperateLog(OperateLog operateLog) {
        //判断是否为分页查询
        if (PageHelperUtil.isPaging(operateLog)) {
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            PageResult<OperateLog> pageResult = PageHelperUtil.pagingQuery(logMapper, methodName, operateLog);
            return ApiResult.success(pageResult);
        } else {
            return ApiResult.success(logMapper.selectOperateLog(operateLog));
        }
    }

    /**
     * ES查询操作日志
     *
     * @author kuangq
     * @date 2020-12-07 23:18
     */
    @Override
    @PermissionAnnotation
    public ApiResult selectOperateLogEs(OperateLog operateLog) {
        SearchSourceQuery searchSourceQuery = new SearchSourceQuery();
        //设置查询全部数据
        searchSourceQuery.setHitAll(true);
        //设置查询索引，对应数据库表名，禁止大写
        searchSourceQuery.setIndex("operate_log");
        //设置查询字段名称，此处为保存时候设置的驼峰式命名
        searchSourceQuery.setFields("uuid,serverIp,clientIp,url,type,path,method,title,creatorId,creatorName,createDeptId,status,elapsedTime,remark,createTime");
        //设置分页数
        searchSourceQuery.setPageNum(operateLog.getPageNum());
        //设置分页大小
        searchSourceQuery.setPageSize(operateLog.getPageSize());
        //设置查询条件
        BoolQueryBuilder boolQueryBuilder = boolQuery();
        //设置查询操作状态
        if (operateLog.getStatus() != null) {
            boolQueryBuilder.must(termQuery("status", operateLog.getStatus()));
        }
        //设置事件标题/方法名/操作人的模糊查询条件
        if (operateLog.getParams().containsKey("queryText")) {
            String queryText = operateLog.getParams().get("queryText").toString();
            //注意后面拼接.keyword
            boolQueryBuilder.must(boolQuery()
                    .should(wildcardQuery("serverIp.keyword", "*" + queryText + "*"))
                    .should(wildcardQuery("clientIp.keyword", "*" + queryText + "*"))
                    .should(wildcardQuery("title.keyword", "*" + queryText + "*"))
                    .should(wildcardQuery("method.keyword", "*" + queryText + "*"))
                    .should(wildcardQuery("creatorName.keyword", "*" + queryText + "*"))
            );
        }
        //创建范围条件
        RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("createTime");
        //开始时间不为空设置大于等于条件
        if (StringUtils.isNotBlank(operateLog.getStartTime())) {
            rangeQueryBuilder.gte(DateUtil.getTimestamp(operateLog.getStartTime(), DatePattern.YMD_HMS_1));
        }
        //结束时间不为空设置小于等于条件
        if (StringUtils.isNotBlank(operateLog.getEndTime())) {
            rangeQueryBuilder.lte(DateUtil.getTimestamp(operateLog.getEndTime(), DatePattern.YMD_HMS_1));
        }
        //设置查询时间范围
        boolQueryBuilder.must(rangeQueryBuilder);
        //设置权限查询条件
        Map<String, Object> params = operateLog.getParams();
        //不为管理员且数据权限不为0时设置该条件过滤
        if (!params.isEmpty() && !(boolean) params.get("isAdmin") && !PermissionType.ALL.getValue().equals(params.get("permissionScope").toString())) {
            String scopeSql = params.get("scopeSql").toString();
            //仅为自身权限时scopeSql为用户本身uuid
            if (PermissionType.ONLY_SELF.getValue().equals(params.get("permissionScope").toString())) {
                //单体条件
                boolQueryBuilder.must(termQuery("creatorId", scopeSql));
            } else {
                //将in语句转换为String[]，去除首尾括号，去除单引号，根据, 分组
                String[] values = scopeSql.substring(1, scopeSql.length() - 1).replaceAll("'", "").split(", ");
                //设置in条件
                boolQueryBuilder.must(termsQuery("createDeptId", values));
            }
        }
        //设置上述查询条件
        searchSourceQuery.setQueryBuilder(boolQueryBuilder);
        //设置查询类型
        if (operateLog.getParams().containsKey("mode")) {
            searchSourceQuery.setMode(operateLog.getParams().get("mode").toString());
        }
        //es查询数据
        return ApiResult.success(ElasticsearchUtil.selectData(searchSourceQuery));
    }

    /**
     * 从ES里查询操作日志的详细出入参
     *
     * @author kuangq
     * @date 2021-03-31 17:29
     */
    @Override
    public ApiResult selectOperateArgs(String uuid, boolean isEs) {
        Map<String, Object> result = isEs ? ElasticsearchUtil.selectData("operate_log", uuid, "inArgs,outArgs") : logMapper.selectOperateArgs(uuid);
        String key = String.format("%s%s", PrefixConst.CONSOLE_LOG, uuid);
        result.put("consoleLog", CacheUtil.KEY.has(key) ? JSON.toJSONString(CacheUtil.LIST.range(key)) : Collections.emptyList());
        return ApiResult.success(result);
    }

    /**
     * 同步操作日志
     *
     * @author kuangq
     * @date 2020-08-01 22:14
     */
    @Override
    @SneakyThrows
    @PermissionAnnotation
    public ApiResult syncOperateLog(OperateLog operateLog) {
        long startTime = System.currentTimeMillis();
        //定义结果集
        List<OperateLog> all = new ArrayList<>();
        //添加线程任务
        List<Callable<List<OperateLog>>> threadTask = new ArrayList<>();
        for (int index = 1; index <= operateLog.getPageNum(); index++) {
            OperateLog finalOperateLog = new OperateLog();
            BeanUtil.copyBean(operateLog, finalOperateLog);
            finalOperateLog.setPageNum(index);
            threadTask.add(() -> {
                List<OperateLog> operateLogList = logMapper.selectOperateLogList(finalOperateLog);
                for (OperateLog log : operateLogList) {
                    if (!ElasticsearchUtil.existsData("operate_log", log.getUuid())) {
                        ElasticsearchUtil.insertData("operate_log", log.getUuid(), log);
                    }
                }
                return operateLogList;
            });
        }
        //获取线程池
        ThreadPoolExecutor threadPoolExecutor = ExecutorUtil.getThreadPoolExecutor();
        try {
            List<Future<List<OperateLog>>> futureList = threadPoolExecutor.invokeAll(threadTask);
            //处理线程返回结果
            if (!threadPoolExecutor.isTerminated()) {
                for (Future<List<OperateLog>> future : futureList) {
                    all.addAll(future.get());
                }
            }
        } finally {
            //关闭线程池
            threadPoolExecutor.shutdown();
        }
        //返回结果计算耗时
        long endTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<String, Object>(2) {{
            put("elapsedTime", CalculateUtil.actuarial((endTime - startTime), 1000, CalculateType.DIVIDE));
            put("dataTotal", all.size());
        }};
        return ApiResult.success(result);
    }

    /**
     * 接口日志周统计
     *
     * @author kuangq
     * @date 2020-08-30 18:57
     */
    @Override
    public ApiResult logWeekStat(Map<String, Object> params) {
        User user = SecurityUtil.getLoginUser().getUser();
        params.putAll(PermissionAspect.getPermissionFilter(user));
        return ApiResult.success(logMapper.logWeekStat(params));
    }

    /**
     * 删除日志信息
     *
     * @author kuangq
     * @date 2020-12-10 14:22
     */
    @Override
    public ApiResult deleteOperateLog(String... uuids) {
        ElasticsearchUtil.deleteData("operate_log", uuids);
        return ApiResult.success(logMapper.deleteOperateLog(uuids));
    }
}