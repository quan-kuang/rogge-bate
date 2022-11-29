package com.loyer.modules.system.service;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.Constant;

/**
 * 常量信息Service
 *
 * @author kuangq
 * @date 2021-11-23 14:20
 */
public interface ConstantService {

    ApiResult saveConstant(Constant constant);

    ApiResult selectConstant(Constant constant);

    ApiResult deleteConstant(Long... ids);
}