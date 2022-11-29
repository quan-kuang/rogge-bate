package com.loyer.modules.system.initialize;

import com.loyer.common.redis.utils.CacheUtil;
import com.loyer.modules.system.constant.PrefixConst;
import com.loyer.modules.system.entity.Constant;
import com.loyer.modules.system.mapper.postgresql.ConstantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * 初始化加载系统常量
 *
 * @author kuangq
 * @date 2021-11-23 16:08
 */
@Component
@DependsOn("cacheUtil")
public class LoadConstant {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ConstantMapper constantMapper;

    @PostConstruct
    public void loadConstant() {
        logger.info("【开始加载系统常量】");
        List<Constant> constantList = constantMapper.selectConstant(null);
        for (Constant constant : constantList) {
            CacheUtil.HASH.put(PrefixConst.CONSTANT, constant.getKey(), constant.getValue());
        }
        logger.info("【系统常量加载完成】");
    }
}