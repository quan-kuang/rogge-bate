package com.loyer.modules.system.mapper.mysql;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * @author kuangq
 * @title DemoMapper
 * @description 测试Mapper
 * @date 2019-08-01 11:02
 */
@Repository("MysqlDemoMapper")
public interface DemoMapper {

    List<HashMap<String, Object>> queryDataBase();

    Integer insertTest(String uuid);
}