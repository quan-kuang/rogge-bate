package com.loyer.modules.system.service;

import com.loyer.common.core.entity.Menu;
import com.loyer.common.dedicine.entity.ApiResult;

/**
 * @author kuangq
 * @title MenuService
 * @description 菜单Service
 * @date 2020-07-30 13:01
 */
public interface MenuService {

    ApiResult saveMenu(Menu menu);

    ApiResult selectMenu(Menu menu);

    ApiResult deleteMenu(String... uuids);

    ApiResult updateMenu(Menu menu);

    ApiResult selectCascade(String... uuids);
}