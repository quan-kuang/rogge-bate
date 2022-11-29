package com.loyer.modules.message.service.impl;

import com.loyer.common.core.enums.CalculateType;
import com.loyer.common.core.utils.common.CalculateUtil;
import com.loyer.common.core.utils.common.ExecutorUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.message.entity.EastMoney;
import com.loyer.modules.message.entity.Stocks;
import com.loyer.modules.message.enums.Plate;
import com.loyer.modules.message.mapper.postgresql.StocksMapper;
import com.loyer.modules.message.service.StocksService;
import com.loyer.modules.message.utils.StocksAnalyseUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 股票信息ServiceImpl
 *
 * @author kuangq
 * @date 2021-09-16 22:45
 */
@Service
public class StocksServiceImpl implements StocksService {

    @Resource
    private StocksMapper stocksMapper;

    /**
     * 获取股票信息
     *
     * @author kuangq
     * @date 2021-09-16 15:31
     */
    @Override
    public ApiResult getList(Stocks.Query query) {
        EastMoney.Response.Data data = null;
        String code = query.getCode();
        String name = query.getName();
        String sortField = query.getSortField();
        String plate = Plate.getValue(query.getPlate());
        //根据股票代码查询
        if (StringUtils.isNotBlank(code)) {
            Optional<Stocks> jsonObjectOptional = StocksAnalyseUtil.getAllData(plate).stream().filter(item -> item.getF12().equals(code)).findFirst();
            if (jsonObjectOptional.isPresent()) {
                data = new EastMoney.Response.Data(1, Collections.singletonList(jsonObjectOptional.get()));
            }
        }
        //根据股票名称模糊查询
        else if (StringUtils.isNotBlank(name)) {
            List<Stocks> result = StocksAnalyseUtil.getAllData(plate).parallelStream().filter(item -> item.getF14().contains(name)).collect(Collectors.toList());
            if (result.size() > 0) {
                data = StocksAnalyseUtil.getPagingInfo(result, query);
            }
        }
        //排序/根据板块查询
        else if (StringUtils.isNotBlank(sortField) || !Plate.ALL.value().equals(plate)) {
            data = StocksAnalyseUtil.getPagingInfo(StocksAnalyseUtil.getAllData(plate), query);
        }
        //无参分页查询
        else {
            EastMoney.Request request = new EastMoney.Request();
            request.setPn(query.getPageNum());
            request.setPz(query.getPageSize());
            request.setUt(StocksAnalyseUtil.getToken());
            request.setFs(plate);
            data = StocksAnalyseUtil.getData(request);
        }
        return ApiResult.success(data);
    }

    /**
     * 采集股票信息保存至数据库
     *
     * @author kuangq
     * @date 2021-09-17 17:57
     */
    @SneakyThrows
    @Override
    public ApiResult collectData() {
        List<Stocks> stocksList = StocksAnalyseUtil.getAllData(Plate.ALL.value());
        int count = (int) Math.ceil(CalculateUtil.actuarial(stocksList.size(), 100, CalculateType.DIVIDE));
        List<Callable<Integer>> threadTask = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            List<Stocks> list = stocksList.subList(index * 100, Math.min((index + 1) * 100, stocksList.size()));
            threadTask.add(() -> stocksMapper.insertStocks(list));
        }
        ThreadPoolExecutor threadPoolExecutor = ExecutorUtil.getThreadPoolExecutor();
        int sum = 0;
        try {
            List<Future<Integer>> futureList = threadPoolExecutor.invokeAll(threadTask);
            if (!threadPoolExecutor.isTerminated()) {
                sum = futureList.stream().mapToInt(item -> {
                    try {
                        return item.get();
                    } catch (Exception e) {
                        return 0;
                    }
                }).sum();
            }
        } finally {
            //关闭线程池
            threadPoolExecutor.shutdown();
        }
        return ApiResult.success(sum);
    }
}