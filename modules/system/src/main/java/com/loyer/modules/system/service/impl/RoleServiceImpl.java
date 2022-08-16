package com.loyer.modules.system.service.impl;

import com.loyer.common.core.entity.Role;
import com.loyer.common.datasource.entity.PageResult;
import com.loyer.common.datasource.utils.PageHelperUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.security.annotation.PermissionAnnotation;
import com.loyer.modules.system.mapper.postgresql.RoleMapper;
import com.loyer.modules.system.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kuangq
 * @title RoleServiceImpl
 * @description 角色ServiceImpl
 * @date 2020-07-30 12:46
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    /**
     * @param role
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 保存角色信息
     * @date 2020-07-30 12:46
     */
    @Override
    @PermissionAnnotation
    @Transactional(transactionManager = "dataSourceTransactionManager", rollbackFor = Exception.class)
    public ApiResult saveRole(Role role) {
        Map<String, Object> result = new HashMap<>(3);
        //保存角色信息
        result.put("saveRole", roleMapper.saveRole(role));
        //先删除关联的角色菜单
        result.put("deleteRoleLink", roleMapper.deleteRoleLink(role.getUuid()));
        //插入角色菜单
        result.put("saveRoleLink", roleMapper.saveRoleLink(role));
        return ApiResult.success(result);
    }

    /**
     * @param role
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 查询角色信息
     * @date 2020-07-30 12:46
     */
    @Override
    @PermissionAnnotation
    public ApiResult selectRole(Role role) {
        //判断是否为分页查询
        if (PageHelperUtil.isPaging(role)) {
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            PageResult<Role> pageResult = PageHelperUtil.pagingQuery(roleMapper, methodName, role);
            return ApiResult.success(pageResult);
        } else {
            return ApiResult.success(roleMapper.selectRole(role));
        }
    }

    /**
     * @param uuids
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 删除角色信息
     * @date 2020-07-30 12:46
     */
    @Override
    @Transactional(transactionManager = "dataSourceTransactionManager", rollbackFor = Exception.class)
    public ApiResult deleteRole(String... uuids) {
        Map<String, Object> result = new HashMap<>(2);
        //删除角色信息
        result.put("deleteRole", roleMapper.deleteRole(uuids));
        //先删除关联的角色菜单
        for (String uuid : uuids) {
            result.put("deleteRoleLink", roleMapper.deleteRoleLink(uuid));
        }
        return ApiResult.success(result);
    }

    /**
     * @param role
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 修改角色信息
     * @date 2020-07-30 14:42
     */
    @Override
    public ApiResult updateRole(Role role) {
        return ApiResult.success(roleMapper.updateRole(role));
    }

    /**
     * @param roleIds
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 查询角色关联菜单/部门ID
     * @date 2020-06-09 9:40
     */
    @Override
    public ApiResult selectRoleLink(String... roleIds) {
        Map<String, Object> result = new HashMap<String, Object>(2) {{
            put("menuIds", roleMapper.selectRoleLink(roleIds, "menu"));
            put("deptIds", roleMapper.selectRoleLink(roleIds, "dept"));
        }};
        return ApiResult.success(result);
    }

    /**
     * @param name
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 根据角色名校验是否存在
     * @date 2020-06-09 9:42
     */
    @Override
    public ApiResult checkRoleExists(String name) {
        return ApiResult.success(roleMapper.checkRoleExists(name));
    }
}