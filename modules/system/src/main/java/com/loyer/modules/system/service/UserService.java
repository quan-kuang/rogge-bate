package com.loyer.modules.system.service;

import com.loyer.common.core.entity.User;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.security.entity.LoginUser;

/**
 * @author kuangq
 * @title UserService
 * @description 用户Service
 * @date 2020-05-13 10:03
 */
public interface UserService {

    ApiResult login(LoginUser loginUser);

    ApiResult messageLogin(User user);

    ApiResult saveUser(User user);

    ApiResult selectUser(User user);

    ApiResult deleteUser(String... uuids);

    ApiResult updateUser(User user);

    ApiResult selectUserById(String uuid);

    ApiResult selectUserBy(User user);

    ApiResult checkUserExists(String account);

    ApiResult auth(User user);
}
