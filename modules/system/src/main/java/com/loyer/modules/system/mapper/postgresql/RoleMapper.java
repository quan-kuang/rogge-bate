package com.loyer.modules.system.mapper.postgresql;

import com.loyer.common.core.entity.Role;

import java.util.List;
import java.util.Set;

/**
 * 角色Mapper
 *
 * @author kuangq
 * @date 2020-07-30 12:46
 */
public interface RoleMapper {

    Integer saveRole(Role role);

    List<Role> selectRole(Role role);

    Integer deleteRole(String... uuids);

    Integer updateRole(Role role);

    Integer checkRoleExists(String name);

    Integer saveRoleLink(Role role);

    Integer deleteRoleLink(String roleId);

    Set<String> selectRoleLink(String[] roleIds, String linkType);

    Set<String> selectPermissions(String... roleIds);

    List<Role> selectRoleByUserId(String userId);
}