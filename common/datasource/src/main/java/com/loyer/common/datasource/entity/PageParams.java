package com.loyer.common.datasource.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页参数
 *
 * @author kuangq
 * @date 2022-01-06 18:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParams {

    private Integer pageNum;

    private Integer pageSize;

    private Class<?> aClass;
}