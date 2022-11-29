package com.loyer.modules.message.entity;

import com.loyer.common.core.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.security.Timestamp;

/**
 * 股票信息实体类
 *
 * @author kuangq
 * @date 2021-09-15 22:13
 */
@Data
@NoArgsConstructor
public class Stocks {

    //自增主键
    private Integer id;
    //最新
    private Double f2;
    //涨幅
    private Double f3;
    //涨跌
    private Double f4;
    //换手率
    private Double f8;
    //市盈率
    private Double f9;
    //量比
    private Double f10;
    //代码
    private String f12;
    //名称
    private String f14;
    //最高
    private Double f15;
    //最低
    private Double f16;
    //开盘
    private Double f17;
    //昨收
    private Double f18;
    //市总值
    private Long f20;
    //市净率
    private Double f23;
    //行业
    private String f100;
    //时间
    private Integer f124;
    //创建时间
    private Timestamp createTime;

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class Query extends BaseEntity {
        String code;
        String name;
        String plate;
        String sortField;
    }
}