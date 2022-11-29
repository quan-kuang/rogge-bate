package com.loyer.modules.system.service;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.Dept;

/**
 * 部门Service
 *
 * @author kuangq
 * @date 2020-09-10 13:42
 */
public interface DeptService {

    ApiResult saveDept(Dept dept);

    ApiResult selectDept(Dept dept);

    ApiResult deleteDept(String... uuids);

    ApiResult selectCascade(String... uuids);
}