package com.loyer.modules.message.controller;

import com.loyer.common.core.annotation.OperateLogAnnotation;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.message.entity.Stocks;
import com.loyer.modules.message.service.StocksService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 股票模块
 *
 * @author kuangq
 * @date 2021-09-16 16:06
 */
@Api(tags = "股票模块")
@RestController
@RequestMapping("stocks")
public class StocksController {

    @Resource
    private StocksService stocksService;

    @OperateLogAnnotation
    @ApiOperation("获取股票信息")
    @PostMapping("getList")
    public ApiResult getList(@RequestBody Stocks.Query query) {
        return stocksService.getList(query);
    }

    @OperateLogAnnotation
    @ApiOperation("采集股票信息")
    @GetMapping("collectData")
    public ApiResult collectData() {
        return stocksService.collectData();
    }
}