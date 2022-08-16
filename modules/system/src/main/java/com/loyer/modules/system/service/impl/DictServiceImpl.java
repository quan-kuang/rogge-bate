package com.loyer.modules.system.service.impl;

import com.loyer.common.datasource.entity.PageResult;
import com.loyer.common.datasource.utils.PageHelperUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.security.annotation.PermissionAnnotation;
import com.loyer.modules.system.entity.Dict;
import com.loyer.modules.system.mapper.postgresql.DictMapper;
import com.loyer.modules.system.service.DictService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author kuangq
 * @title DictServiceImpl
 * @description 字典ServiceImpl
 * @date 2020-05-16 17:03
 */
@Service
public class DictServiceImpl implements DictService {

    @Resource
    private DictMapper dictMapper;

    /**
     * @param dict
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 保存字典信息
     * @date 2020-05-22 19:15
     */
    @Override
    @PermissionAnnotation
    public ApiResult saveDict(Dict dict) {
        if (dict.getSort() == null) {
            Integer sort = dictMapper.selectDictSort(dict.getParentId());
            dict.setSort(sort == null ? 1 : sort + 1);
        }
        return ApiResult.success(dictMapper.saveDict(dict));
    }

    /**
     * @param dict
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 查询字典信息
     * @date 2020-05-22 19:15
     */
    @Override
    public ApiResult selectDict(Dict dict) {
        //判断是否为分页查询
        if (PageHelperUtil.isPaging(dict)) {
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            PageResult<Dict> pageResult = PageHelperUtil.pagingQuery(dictMapper, methodName, dict);
            return ApiResult.success(pageResult);
        } else {
            return ApiResult.success(dictMapper.selectDict(dict));
        }
    }

    /**
     * @param uuids
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 删除字典信息
     * @date 2020-05-22 19:15
     */
    @Override
    public ApiResult deleteDict(String... uuids) {
        return ApiResult.success(dictMapper.deleteDict(uuids));
    }

    /**
     * @param uuids
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 字典级联查询
     * @date 2020-07-30 16:11
     */
    @Override
    public ApiResult selectCascade(String... uuids) {
        return ApiResult.success(dictMapper.selectCascade(uuids));
    }

    /**
     * @param dict
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 字典项存在校验
     * @date 2020-05-22 19:16
     */
    @Override
    public ApiResult checkDictExists(Dict dict) {
        return ApiResult.success(dictMapper.checkDictExists(dict));
    }
}