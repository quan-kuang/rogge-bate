package com.loyer.modules.message.service;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.message.entity.Stocks;

/**
 * 股票信息Service
 *
 * @author kuangq
 * @date 2021-09-16 22:45
 */
public interface StocksService {

    ApiResult getList(Stocks.Query query);

    ApiResult collectData();
}