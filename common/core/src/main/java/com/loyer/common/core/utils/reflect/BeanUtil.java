package com.loyer.common.core.utils.reflect;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Bean工具类
 *
 * @author kuangq
 * @date 2020-12-16 17:27
 */
public class BeanUtil extends BeanUtils {

    //Bean方法名中属性名开始的下标
    private static final int BEAN_METHOD_INDEX = 3;

    //匹配getter方法的正则表达式
    private static final Pattern GET_PATTERN = Pattern.compile("get(\\p{javaUpperCase}\\w*)");

    //匹配setter方法的正则表达式
    private static final Pattern SET_PATTERN = Pattern.compile("set(\\p{javaUpperCase}\\w*)");

    /**
     * Bean属性复制，将source属性复制给target
     *
     * @author kuangq
     * @date 2020-12-16 17:30
     */
    public static void copyBean(Object source, Object target) {
        copyProperties(source, target);
    }

    /**
     * 获取对象的get方法
     *
     * @author kuangq
     * @date 2020-12-16 17:40
     */
    public static List<Method> getGetMethods(Object object) {
        //获取所有方法
        Method[] methods = object.getClass().getMethods();
        if (methods.length > 0) {
            //过滤对象所有属性的get方法
            return Arrays.stream(methods).filter(method ->
                    GET_PATTERN.matcher(method.getName()).matches() && method.getParameterTypes().length == 0 && !object.getClass().getName().equals(method.getName())
            ).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 获取对象的set方法
     *
     * @author kuangq
     * @date 2020-12-16 17:40
     */
    public static List<Method> getSetMethods(Object object) {
        //获取所有方法
        Method[] methods = object.getClass().getMethods();
        if (methods.length > 0) {
            //过滤对象所有属性的set方法
            return Arrays.stream(methods).filter(method ->
                    SET_PATTERN.matcher(method.getName()).matches() && method.getParameterTypes().length == 1
            ).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 校验两个方法的属性名是否相等，如getName()和setName()属性名一样，getName()和setAge()属性名不一样
     *
     * @author kuangq
     * @date 2020-12-16 17:42
     */
    public static boolean isMethodPropertyEquals(String getName, String setName) {
        return getName.substring(BEAN_METHOD_INDEX).equals(setName.substring(BEAN_METHOD_INDEX));
    }
}