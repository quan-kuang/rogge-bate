package com.loyer.modules.system.service;

import com.loyer.common.core.entity.User;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.system.entity.OnLineUser;

import javax.servlet.http.HttpServletRequest;

/**
 * 在线用户Service
 *
 * @author kuangq
 * @date 2020-12-15 17:45
 */
public interface OnLineUserService {

    void asyncSaveOnLineUser(HttpServletRequest httpServletRequest, String token, User user);

    ApiResult selectOnLineUser(OnLineUser onLineUser);

    ApiResult deleteOnLineUser(String... uuids);
}