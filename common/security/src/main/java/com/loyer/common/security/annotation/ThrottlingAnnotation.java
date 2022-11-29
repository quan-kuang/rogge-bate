package com.loyer.common.security.annotation;

import com.loyer.common.security.constant.DefaultConst;
import com.loyer.common.security.enums.ThrottlingType;

import java.lang.annotation.*;

/**
 * 接口限流注解
 *
 * @author kuangq
 * @date 2020-11-05 14:04
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ThrottlingAnnotation {

    //周期，单位秒
    int cycle() default DefaultConst.THROTTLING_CYCLE;

    //周期类的访问次数
    int count() default DefaultConst.THROTTLING_COUNT;

    //限流类型
    ThrottlingType throttlingType() default ThrottlingType.SERVE;
}