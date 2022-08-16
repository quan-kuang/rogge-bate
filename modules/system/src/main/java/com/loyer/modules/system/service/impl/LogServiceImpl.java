package com.loyer.modules.system.service.impl;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * @author kuangq
 * @title LogServiceImpl
 * @description 日志ServiceImpl
 * @date 2020-06-02 16:50
 */
@Service
public class LogServiceImpl implements LogService {

    @Resource
    private LogMapper logMapper;

    /**
     * @param operateLog
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 保存操作日志
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
     * @param operateLog
     * @return void
     * @author kuangq
     * @description 设置操作用户
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
                user = SecurityUtil.getLoginUser(operateLog.getToken()).getUser();
            }
        } catch (Exception e) {
            user = new User();
        }
        operateLog.setCreatorId(user.getUuid());
        operateLog.setCreatorName(user.getName());
        operateLog.setCreateDeptId(user.getDeptId());
    }

    /**
     * @param operateLog
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 查询操作日志
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
     * @param operateLog
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description ES查询操作日志
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
        searchSourceQuery.setFields("uuid,ip,url,type,path,method,title,creatorId,creatorName,createDeptId,status,elapsedTime,remark,createTime");
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
                    .should(wildcardQuery("ip.keyword", "*" + queryText + "*"))
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
     * @param uuid
     * @param isEs
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 从ES里查询操作日志的详细出入参
     * @date 2021-03-31 17:29
     */
    @Override
    public ApiResult selectOperateArgs(String uuid, boolean isEs) {
        if (isEs) {
            return ApiResult.success(ElasticsearchUtil.selectData("operate_log", uuid, "inArgs,outArgs"));
        }
        return ApiResult.success(logMapper.selectOperateArgs(uuid));
    }

    /**
     * @param operateLog
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 同步操作日志
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
        ExecutorService executorService = ExecutorUtil.getExecutorService();
        try {
            List<Future<List<OperateLog>>> futureList = executorService.invokeAll(threadTask);
            //处理线程返回结果
            if (!executorService.isTerminated()) {
                for (Future<List<OperateLog>> future : futureList) {
                    all.addAll(future.get());
                }
            }
        } finally {
            //关闭线程池
            executorService.shutdown();
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
     * @param params
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 接口日志周统计
     * @date 2020-08-30 18:57
     */
    @Override
    public ApiResult logWeekStat(Map<String, Object> params) {
        User user = SecurityUtil.getLoginUser().getUser();
        params.putAll(PermissionAspect.getPermissionFilter(user));
        return ApiResult.success(logMapper.logWeekStat(params));
    }

    /**
     * @param uuids
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 删除日志信息
     * @date 2020-12-10 14:22
     */
    @Override
    public ApiResult deleteOperateLog(String... uuids) {
        ElasticsearchUtil.deleteData("operate_log", uuids);
        return ApiResult.success(logMapper.deleteOperateLog(uuids));
    }
}