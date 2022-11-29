package com.loyer.modules.system.service.impl;

import com.loyer.common.datasource.entity.PageResult;
import com.loyer.common.datasource.utils.PageHelperUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.redis.utils.CacheUtil;
import com.loyer.modules.system.constant.PrefixConst;
import com.loyer.modules.system.entity.Constant;
import com.loyer.modules.system.mapper.postgresql.ConstantMapper;
import com.loyer.modules.system.service.ConstantService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 常量信息ServiceImpl
 *
 * @author kuangq
 * @date 2021-11-23 14:20
 */
@Service
public class ConstantServiceImpl implements ConstantService {

    @Resource
    private ConstantMapper constantMapper;

    /**
     * 保存常量信息
     *
     * @author kuangq
     * @date 2021-11-23 14:20
     */
    @Override
    public ApiResult saveConstant(Constant constant) {
        int flag = constantMapper.saveConstant(constant);
        if (flag > 0) {
            CacheUtil.HASH.put(PrefixConst.CONSTANT, constant.getKey(), constant.getValue());
        }
        return ApiResult.success(flag);
    }

    /**
     * 查询常量信息
     *
     * @author kuangq
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
     * 删除常量信息
     *
     * @author kuangq
     * @date 2021-11-23 14:20
     */
    @Override
    public ApiResult deleteConstant(Long... ids) {
        List<Constant> constantList = constantMapper.selectConstantByIds(ids);
        int flag = constantMapper.deleteConstant(ids);
        if (flag > 0) {
            constantList.forEach(constant -> CacheUtil.HASH.delete(PrefixConst.CONSTANT, constant.getKey()));
        }
        return ApiResult.success(constantMapper.deleteConstant(ids));
    }
}