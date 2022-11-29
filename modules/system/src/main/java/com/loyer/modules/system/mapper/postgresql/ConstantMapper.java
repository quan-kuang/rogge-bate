package com.loyer.modules.system.mapper.postgresql;

import com.loyer.modules.system.entity.Constant;

import java.util.List;

/**
 * 常量信息Mapper
 *
 * @author kuangq
 * @date 2021-11-23 14:20
 */
public interface ConstantMapper {

    Integer saveConstant(Constant constant);

    List<Constant> selectConstant(Constant constant);

    List<Constant> selectConstantByIds(Long... ids);

    Integer deleteConstant(Long... ids);
}