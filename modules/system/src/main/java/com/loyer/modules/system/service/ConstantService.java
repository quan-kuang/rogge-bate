package com.loyer.modules.system.service;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.Constant;

/**
 * @author kuangq
 * @title ConstantService
 * @description 常量信息Service
 * @date 2021-11-23 14:20
 */
public interface ConstantService {

    ApiResult saveConstant(Constant constant);

    ApiResult selectConstant(Constant constant);

    ApiResult deleteConstant(Long... ids);
}