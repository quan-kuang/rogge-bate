package com.loyer.modules.system.mapper.postgresql;

import com.loyer.modules.system.entity.Dept;

import java.util.List;
import java.util.Set;

/**
 * 部门Mapper
 *
 * @author kuangq
 * @date 2020-09-10 13:42
 */
public interface DeptMapper {

    Integer saveDept(Dept dept);

    List<Dept> selectDept(Dept dept);

    Integer deleteDept(String... uuids);

    Set<String> selectCascade(String... uuids);

    Integer selectDeptSort(String parentId);
}