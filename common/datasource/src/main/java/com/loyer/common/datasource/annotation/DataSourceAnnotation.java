package com.loyer.common.datasource.annotation;

import com.loyer.common.datasource.enums.DataSourceType;

import java.lang.annotation.*;

/**
 * 自定义数据源类型注解
 *
 * @author kuangq
 * @date 2019-11-20 17:54
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataSourceAnnotation {

    //指定切换数据源
    DataSourceType value() default DataSourceType.POSTGRESQL;

    //是否需要切换
    boolean isToggle() default true;
}