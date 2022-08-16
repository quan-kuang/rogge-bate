package com.loyer.modules.message.mapper.postgresql;

import com.loyer.modules.message.entity.Stocks;

import java.util.List;

/**
 * @author kuangq
 * @title StocksMapper
 * @description 股票信息Mapper
 * @date 2021-09-16 22:45
 */
public interface StocksMapper {

    Integer insertStocks(List<Stocks> stocksList);
}