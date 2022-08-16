package com.loyer.modules.system.service.impl;

import com.loyer.common.datasource.entity.PageResult;
import com.loyer.common.datasource.utils.PageHelperUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.security.annotation.PermissionAnnotation;
import com.loyer.modules.system.entity.Dept;
import com.loyer.modules.system.mapper.postgresql.DeptMapper;
import com.loyer.modules.system.service.DeptService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author kuangq
 * @title DeptServiceImpl
 * @description 部门ServiceImpl
 * @date 2020-09-10 13:42
 */
@Service
public class DeptServiceImpl implements DeptService {

    @Resource
    private DeptMapper deptMapper;

    /**
     * @param dept
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 保存部门信息
     * @date 2020-09-10 13:42
     */
    @Override
    @PermissionAnnotation
    public ApiResult saveDept(Dept dept) {
        if (dept.getSort() == null) {
            Integer sort = deptMapper.selectDeptSort(dept.getParentId());
            dept.setSort(sort == null ? 1 : sort + 1);
        }
        return ApiResult.success(deptMapper.saveDept(dept));
    }

    /**
     * @param dept
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 查询部门信息
     * @date 2020-09-10 13:42
     */
    @Override
    @PermissionAnnotation
    public ApiResult selectDept(Dept dept) {
        //判断是否为分页查询
        if (PageHelperUtil.isPaging(dept)) {
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            PageResult<Dept> pageResult = PageHelperUtil.pagingQuery(deptMapper, methodName, dept);
            return ApiResult.success(pageResult);
        } else {
            return ApiResult.success(deptMapper.selectDept(dept));
        }
    }

    /**
     * @param uuids
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 删除部门信息
     * @date 2020-09-10 13:42
     */
    @Override
    public ApiResult deleteDept(String... uuids) {
        return ApiResult.success(deptMapper.deleteDept(uuids));
    }

    /**
     * @param uuids
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 部门级联查询
     * @date 2020-09-10 21:33
     */
    @Override
    public ApiResult selectCascade(String... uuids) {
        return ApiResult.success(deptMapper.selectCascade(uuids));
    }
}