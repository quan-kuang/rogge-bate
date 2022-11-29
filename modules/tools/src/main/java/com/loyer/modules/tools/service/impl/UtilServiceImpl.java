package com.loyer.modules.tools.service.impl;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.tools.entity.Sequence;
import com.loyer.modules.tools.mapper.mysql.UtilMapper;
import com.loyer.modules.tools.service.UtilService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 工具ServiceImpl
 *
 * @author kuangq
 * @date 2021-06-24 18:50
 */
@Service
public class UtilServiceImpl implements UtilService {

    @Resource
    private UtilMapper utilMapper;

    /**
     * 获取序列号
     *
     * @author kuangq
     * @date 2021-06-27 13:32
     */
    @Override
    public ApiResult getSequence() {
        Sequence sequence = new Sequence();
        utilMapper.insert(sequence);
        return ApiResult.success(sequence.getValue());
    }
}