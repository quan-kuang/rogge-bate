package com.loyer.modules.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * oracle数据库资源
 *
 * @author kuangq
 * @date 2019-08-01 17:08
 */
@Data
@NoArgsConstructor
public class OracleResource {

    //数据库属性类型
    private String objectType;
    //该属性数目
    private Integer objectCount;
}