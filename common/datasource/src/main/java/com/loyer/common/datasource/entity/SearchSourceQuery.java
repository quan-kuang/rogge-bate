package com.loyer.common.datasource.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;

/**
 * ES查询条件实体类
 *
 * @author kuangq
 * @date 2020-12-07 17:45
 */
@Data
@NoArgsConstructor
public class SearchSourceQuery {

    //ES查询方式API/DSL
    private String mode;

    //索引名称
    private String index;

    //是否返回数据源
    private Boolean fetchSource;

    //是否查询全部数据
    private Boolean hitAll;

    //查询字段名称，缺省查询全部，多个字段用,分割
    private String fields;

    //索引起始值
    private Integer pageNum;

    //索引范围
    private Integer pageSize;

    //排序字段
    private String sortField;

    //排序类型
    private SortOrder sortOrder;

    //查询条件
    private QueryBuilder queryBuilder;

    //聚合查询
    private AggregationBuilder aggregationBuilder;
}