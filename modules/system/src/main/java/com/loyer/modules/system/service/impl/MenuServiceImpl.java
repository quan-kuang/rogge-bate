package com.loyer.modules.system.service.impl;

import com.loyer.common.core.entity.Menu;
import com.loyer.common.datasource.entity.PageResult;
import com.loyer.common.datasource.utils.PageHelperUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.security.annotation.PermissionAnnotation;
import com.loyer.modules.system.mapper.postgresql.MenuMapper;
import com.loyer.modules.system.service.MenuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author kuangq
 * @title MenuServiceImpl
 * @description 菜单ServiceImpl
 * @date 2020-07-30 13:01
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Resource
    private MenuMapper menuMapper;

    /**
     * @param menu
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 保存菜单信息
     * @date 2020-07-30 13:01
     */
    @Override
    @PermissionAnnotation
    public ApiResult saveMenu(Menu menu) {
        if (menu.getSort() == null) {
            Integer sort = menuMapper.selectMenuSort(menu.getParentId());
            menu.setSort(sort == null ? 1 : sort + 1);
        }
        return ApiResult.success(menuMapper.saveMenu(menu));
    }

    /**
     * @param menu
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 查询菜单信息
     * @date 2020-07-30 13:01
     */
    @Override
    @PermissionAnnotation
    public ApiResult selectMenu(Menu menu) {
        //判断是否为分页查询
        if (PageHelperUtil.isPaging(menu)) {
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            PageResult<Menu> pageResult = PageHelperUtil.pagingQuery(menuMapper, methodName, menu);
            return ApiResult.success(pageResult);
        } else {
            return ApiResult.success(menuMapper.selectMenu(menu));
        }
    }

    /**
     * @param uuids
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 删除菜单信息
     * @date 2020-07-30 13:01
     */
    @Override
    public ApiResult deleteMenu(String... uuids) {
        return ApiResult.success(menuMapper.deleteMenu(uuids));
    }

    /**
     * @param menu
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 修改菜单信息
     * @date 2020-07-30 14:51
     */
    @Override
    public ApiResult updateMenu(Menu menu) {
        return ApiResult.success(menuMapper.updateMenu(menu));
    }

    /**
     * @param menuIds
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 菜单级联查询
     * @date 2020-06-17 18:05
     */
    @Override
    public ApiResult selectCascade(String... menuIds) {
        return ApiResult.success(menuMapper.selectCascade(menuIds));
    }
}