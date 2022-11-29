package com.loyer.common.security.annotation;

import java.lang.annotation.*;

/**
 * 自定义权限注解
 *
 * @author kuangq
 * @date 2019-11-20 17:54
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PermissionAnnotation {

}