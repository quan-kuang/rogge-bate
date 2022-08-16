package com.loyer.modules.message.utils;

import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.enums.CalculateType;
import com.loyer.common.core.utils.common.CalculateUtil;
import com.loyer.common.core.utils.common.ExecutorUtil;
import com.loyer.common.core.utils.common.StringUtil;
import com.loyer.common.core.utils.reflect.BeanUtil;
import com.loyer.common.core.utils.request.HttpUtil;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.modules.message.entity.EastMoney;
import com.loyer.modules.message.entity.Stocks;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author kuangq
 * @title StocksAnalyseUtil
 * @description 股票信息分析工具类（数据来源：http://data.eastmoney.com/zjlx/detail.html）
 * @date 2021-09-15 22:11
 */
public class StocksAnalyseUtil {

    //获取TOKEN
    private static final String GET_TOKEN_URL = "https://data.eastmoney.com/newstatic/js/zjlx/detail.js";
    //获取股票信息
    private static final String GET_DATA_URL = "https://push2.eastmoney.com/api/qt/clist/get";

    /**
     * @param
     * @return java.lang.String
     * @author kuangq
     * @description 获取token
     * @date 2021-09-16 15:16
     */
    public static String getToken() {
        String result = HttpUtil.doGet(GET_TOKEN_URL, String.class);
        Optional<String> optionalString = Arrays.stream(result.split(SpecialCharsConst.LINE_FEED)).filter(item -> item.contains("ut:")).findFirst();
        return optionalString.map(s -> StringUtils.substringBetween(s, "ut: \"", "\"")).orElse(null);
    }

    /**
     * @param request
     * @return com.loyer.modules.message.entity.EastMoney.Response.Data
     * @author kuangq
     * @description 获取股票信息
     * @date 2021-09-16 15:16
     */
    public static EastMoney.Response.Data getData(EastMoney.Request request) {
        EastMoney.Response response = HttpUtil.doGet(GET_DATA_URL, request, EastMoney.Response.class);
        return response.getData();
    }

    /**
     * @param plate
     * @return java.util.List<com.loyer.modules.message.entity.Stocks>
     * @author kuangq
     * @description 查询全部股票信息
     * @date 2021-09-16 15:16
     */
    @SneakyThrows
    public static List<Stocks> getAllData(String plate) {
        //定义结果集
        List<Stocks> all = new ArrayList<>();
        //获取线程池
        ExecutorService executorService = ExecutorUtil.getExecutorService();
        try {
            List<Future<List<Stocks>>> futureList = executorService.invokeAll(getThreadTask(plate));
            //处理线程返回结果
            if (!executorService.isTerminated()) {
                for (Future<List<Stocks>> future : futureList) {
                    all.addAll(future.get());
                }
            }
        } finally {
            //关闭线程池
            executorService.shutdown();
        }
        return all;
    }

    /**
     * @param plate
     * @return java.util.List<java.util.concurrent.Callable < java.util.List < com.loyer.modules.message.entity.Stocks>>>
     * @author kuangq
     * @description 获取线程任务
     * @date 2021-09-16 15:30
     */
    private static List<Callable<List<Stocks>>> getThreadTask(String plate) {
        int pageSize = 100;
        //获取token，count
        String token = getToken();
        EastMoney.Request request = new EastMoney.Request(token, 1, 1, plate);
        int total = getData(request).getTotal();
        int count = (int) Math.ceil(CalculateUtil.actuarial(total, pageSize, CalculateType.DIVIDE));
        //添加线程任务
        List<Callable<List<Stocks>>> threadTask = new ArrayList<>();
        for (int index = 1; index <= count; index++) {
            EastMoney.Request finalRequest = new EastMoney.Request(token, index, pageSize, plate);
            threadTask.add(() -> getData(finalRequest).getDiff());
        }
        return threadTask;
    }

    /**
     * @param all
     * @param query
     * @return com.loyer.modules.message.entity.EastMoney.Response.Data
     * @author kuangq
     * @description 结果集分页排序
     * @date 2021-09-16 15:15
     */
    public static EastMoney.Response.Data getPagingInfo(List<Stocks> all, Stocks.Query query) {
        String sortField = query.getSortField();
        if (StringUtils.isNotBlank(sortField)) {
            all.sort((m, n) -> -Double.compare(getFiledValue(m, sortField), getFiledValue(n, sortField)));
        }
        int fromIndex = (query.getPageNum() - 1) * query.getPageSize();
        int toIndex = query.getPageNum() * query.getPageSize();
        List<Stocks> result = all.subList(fromIndex > all.size() ? 0 : fromIndex, Math.min(toIndex, all.size()));
        return new EastMoney.Response.Data(all.size(), result);
    }

    /**
     * @param object
     * @param sortField
     * @return java.lang.Double
     * @author kuangq
     * @description 获取实例指定元素值
     * @date 2021-09-17 10:12
     */
    @SneakyThrows
    private static Double getFiledValue(Object object, String sortField) {
        Optional<Method> method = BeanUtil.getGetMethods(object).stream().filter(item -> StringUtils.endsWith(item.getName(), StringUtil.firstUpper(sortField))).findFirst();
        if (!method.isPresent()) {
            throw new BusinessException(HintEnum.HINT_1029, sortField);
        }
        return (Double) method.get().invoke(object);
    }
}