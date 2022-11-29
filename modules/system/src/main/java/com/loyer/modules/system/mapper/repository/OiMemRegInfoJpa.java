package com.loyer.modules.system.mapper.repository;

import com.loyer.modules.system.entity.OiMemRegInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 接口定义（接口名遵循JPA范式）
 *
 * @author kuangq
 * @date 2019-11-15 15:12
 */
public interface OiMemRegInfoJpa extends JpaRepository<OiMemRegInfo, String> {

    List<OiMemRegInfo> findByAcountId(String acountId);

    List<OiMemRegInfo> findByRealNameLike(String realName);
}