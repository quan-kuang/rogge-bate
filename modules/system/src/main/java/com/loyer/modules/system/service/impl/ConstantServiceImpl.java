package com.loyer.modules.system.service.impl;

import com.loyer.common.datasource.entity.PageResult;
import com.loyer.common.datasource.utils.PageHelperUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.redis.utils.RedisUtil;
import com.loyer.modules.system.constant.PrefixConst;
import com.loyer.modules.system.entity.Constant;
import com.loyer.modules.system.mapper.postgresql.ConstantMapper;
import com.loyer.modules.system.service.ConstantService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kuangq
 * @title ConstantServiceImpl
 * @description 常量信息ServiceImpl
 * @date 2021-11-23 14:20
 */
@Service
public class ConstantServiceImpl implements ConstantService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ConstantMapper constantMapper;

    /**
     * @param constant
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 保存常量信息
     * @date 2021-11-23 14:20
     */
    @Override
    public ApiResult saveConstant(Constant constant) {
        int flag = constantMapper.saveConstant(constant);
        if (flag > 0) {
            redisUtil.setMap(PrefixConst.CONSTANT, constant.getKey(), constant.getValue());
        }
        return ApiResult.success(flag);
    }

    /**
     * @param constant
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 查询常量信息
     * @date 2021-11-23 14:20
     */
    @Override
    public ApiResult selectConstant(Constant constant) {
        //判断是否为分页查询
        if (PageHelperUtil.isPaging(constant)) {
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            PageResult<Constant> pageResult = PageHelperUtil.pagingQuery(constantMapper, methodName, constant);
            return ApiResult.success(pageResult);
        } else {
            return ApiResult.success(constantMapper.selectConstant(constant));
        }
    }

    /**
     * @param ids
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 删除常量信息
     * @date 2021-11-23 14:20
     */
    @Override
    public ApiResult deleteConstant(Long... ids) {
        List<Constant> constantList = constantMapper.selectConstantByIds(ids);
        int flag = constantMapper.deleteConstant(ids);
        if (flag > 0) {
            constantList.forEach(constant -> redisUtil.deleteMap(PrefixConst.CONSTANT, constant.getKey()));
        }
        return ApiResult.success(constantMapper.deleteConstant(ids));
    }
}