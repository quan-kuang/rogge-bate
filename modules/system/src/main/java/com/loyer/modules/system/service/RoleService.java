package com.loyer.modules.system.service;

import com.loyer.common.core.entity.Role;
import com.loyer.common.dedicine.entity.ApiResult;

/**
 * 角色Service
 *
 * @author kuangq
 * @date 2020-07-30 12:46
 */
public interface RoleService {

    ApiResult saveRole(Role role);

    ApiResult selectRole(Role role);

    ApiResult deleteRole(String... uuids);

    ApiResult updateRole(Role role);

    ApiResult selectRoleLink(String... roleIds);

    ApiResult checkRoleExists(String name);
}