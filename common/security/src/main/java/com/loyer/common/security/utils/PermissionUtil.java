package com.loyer.common.security.utils;

import com.alibaba.fastjson.JSON;
import com.loyer.common.core.entity.Menu;
import com.loyer.common.core.entity.User;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.security.entity.LoginUser;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Security自定义权限工具类
 *
 * @author kuangq
 * @date 2020-08-16 0:16
 */
@Component("pu")
public class PermissionUtil {

    /**
     * 校验是否具有传入的条件的任一权限
     *
     * @author kuangq
     * @date 2020-08-16 16:41
     */
    public boolean hasAnyPermissions(String... permissions) {
        Set<String> permissionsSet = getPermissions(permissions);
        if (permissionsSet != null) {
            for (String permission : permissions) {
                if (permissionsSet.stream().anyMatch(permission::equals)) {
                    return true;
                }
            }
            forbid(String.format("需要任一权限%s", JSON.toJSONString(permissions)));
        }
        return true;
    }

    /**
     * 校验是否具备传入的所有权限
     *
     * @author kuangq
     * @date 2020-08-16 0:19
     */
    public boolean hasAllPermissions(String... permissions) {
        Set<String> permissionsSet = getPermissions(permissions);
        if (permissionsSet != null) {
            for (String permission : permissions) {
                if (permissionsSet.stream().noneMatch(permission::equals)) {
                    forbid(String.format("需要所有权限%s", JSON.toJSONString(permissions)));
                }
            }
        }
        return true;
    }

    /**
     * 获取用户菜单列表中的权限标识
     *
     * @author kuangq
     * @date 2020-08-16 16:43
     */
    private Set<String> getPermissions(String... permissions) {
        //校验入参是否为空
        if (permissions == null || permissions.length == 0) {
            forbid("接口未设置权限标识");
        }
        //校验用户信息是否失效
        LoginUser loginUser = SecurityUtil.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            forbid("登录用户认证已失效");
        }
        User user = loginUser.getUser();
        //校验是否具有管理员权限
        if (user.checkIsAdmin()) {
            return null;
        }
        //校验权限菜单是否为空
        if (CollectionUtils.isEmpty(user.getMenuList())) {
            forbid("用户未设置权限菜单");
        }
        //遍历出MenuList中的权限字段，采用SET收集去重
        return user.getMenuList().stream().map(Menu::getPermission).collect(Collectors.toSet());
    }

    /**
     * 避免返回FALSE会抛出AccessDeniedException异常，直接在全局异常处理器中处理
     *
     * @author kuangq
     * @date 2020-12-10 17:19
     */
    private void forbid(Object message) {
        throw new BusinessException(HintEnum.HINT_1077, message);
    }
}