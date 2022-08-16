package com.loyer.common.datasource.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author kuangq
 * @title PageResult
 * @description 分页结果
 * @date 2022-01-06 18:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private long total;

    private List<T> list;
}