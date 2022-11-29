package com.loyer.modules.system.mapper.postgresql;

import com.loyer.modules.system.entity.Dict;

import java.util.List;
import java.util.Set;

/**
 * 字典Mapper
 *
 * @author kuangq
 * @date 2020-05-16 17:04
 */
public interface DictMapper {

    Integer saveDict(Dict dict);

    List<Dict> selectDict(Dict dict);

    Integer deleteDict(String... uuids);

    Integer selectDictSort(String parentId);

    Set<String> selectCascade(String... uuids);

    Integer checkDictExists(Dict dict);
}