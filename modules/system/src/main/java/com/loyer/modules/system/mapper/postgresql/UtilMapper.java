package com.loyer.modules.system.mapper.postgresql;

import com.loyer.modules.system.entity.FieldExplain;
import com.loyer.modules.system.entity.TableExplain;

import java.util.List;

/**
 * 工具Mapper
 *
 * @author kuangq
 * @date 2020-06-27 11:19
 */
public interface UtilMapper {

    List<String> selectSchemaName();

    List<TableExplain> selectTableExplain(TableExplain tableExplain);

    String selectPrimaryKey(int tableOid);

    List<FieldExplain> selectFieldExplain(int tableOid);
}