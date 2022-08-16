package com.loyer.modules.system.mapper.postgresql;

import com.loyer.common.core.entity.Menu;

import java.util.List;
import java.util.Set;

/**
 * @author kuangq
 * @title MenuMapper
 * @description 菜单Mapper
 * @date 2020-07-30 13:01
 */
public interface MenuMapper {

    Integer saveMenu(Menu menu);

    List<Menu> selectMenu(Menu menu);

    Integer deleteMenu(String... uuids);

    Integer updateMenu(Menu menu);

    Integer selectMenuSort(String parentId);

    Set<String> selectCascade(String... uuids);
}