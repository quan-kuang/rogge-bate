package com.loyer.common.core.annotation;

import java.lang.annotation.*;

/**
 * @author kuangq
 * @title OperateLogAnnotation
 * @description 操作日志注解
 * @date 2020-05-27 14:22
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface OperateLogAnnotation {

    //接口名称
    String value() default "";

    //是否保存入参
    boolean isSaveInArgs() default true;

    //是否保存出参
    boolean isSaveOutArgs() default true;
}