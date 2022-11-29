package com.loyer.modules.system.mapper.mysql;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * 测试Mapper
 *
 * @author kuangq
 * @date 2019-08-01 11:02
 */
@Repository("MysqlDemoMapper")
public interface DemoMapper {

    List<HashMap<String, Object>> queryDataBase();

    Integer insertTest(String uuid);
}