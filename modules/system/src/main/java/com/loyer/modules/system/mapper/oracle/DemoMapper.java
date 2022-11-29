package com.loyer.modules.system.mapper.oracle;

import com.loyer.modules.system.entity.OracleResource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 测试Mapper
 *
 * @author kuangq
 * @date 2019-08-01 11:02
 */
@Repository("OracleDemoMapper")
public interface DemoMapper {

    List<OracleResource> queryDataBase();

    Integer insertTest(String uuid);
}