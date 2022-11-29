package com.loyer.modules.tools.utils;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.modules.tools.entity.MethodEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 根据反射机制动态调用方法
 *
 * @author kuangq
 * @date 2019-10-10 12:46
 */
public class MethodUtil {

    private static final String PUBLIC = "public";
    private static final String PRIVATE = "private";
    private static final String PROTECTED = "protected";
    private static final String BYTE = "byte";
    private static final String SHORT = "short";
    private static final String INT = "int";
    private static final String LONG = "long";
    private static final String FLOAT = "float";
    private static final String DOUBLE = "double";
    private static final String CHAR = "char";
    private static final String BOOLEAN = "boolean";

    /**
     * 获取权限修饰符
     *
     * @author kuangq
     * @date 2019-12-13 10:36
     */
    private static String getPermission(int mod) {
        if (Modifier.isPublic(mod)) {
            return PUBLIC;
        } else if (Modifier.isPrivate(mod)) {
            return PRIVATE;
        } else {
            return PROTECTED;
        }
    }

    /**
     * 类类型数组转String[]
     *
     * @author kuangq
     * @date 2019-12-13 10:37
     */
    private static String[] getParameterTypes(Class<?>[] classes) {
        String[] parameterTypes = new String[classes.length];
        for (int i = 0; i < classes.length; i++) {
            parameterTypes[i] = classes[i].getName();
        }
        return parameterTypes;
    }

    /**
     * 获取方法上注解
     *
     * @author kuangq
     * @date 2019-12-13 14:13
     */
    private static List<Map<String, String>> getAnnotation(Method method) {
        List<Map<String, String>> annotations = new ArrayList<>();
        Map<String, String> map = new HashMap<>(1);
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            if (annotation.annotationType().equals(ApiOperation.class)) {
                map.put("description", method.getAnnotation(ApiOperation.class).value());
            } else if (annotation.annotationType().equals(GetMapping.class)) {
                map.put("get", method.getAnnotation(GetMapping.class).value()[0]);
            } else if (annotation.annotationType().equals(PostMapping.class)) {
                map.put("post", method.getAnnotation(PostMapping.class).value()[0]);
            }
        }
        annotations.add(map);
        return annotations;
    }

    /**
     * 根据完整类名获取该类下所有非私有方法
     *
     * @author kuangq
     * @date 2019-12-13 10:37
     */
    public static Set<MethodEntity> getMethod(String className) {
        try {
            Set<MethodEntity> methodEntitySet = new HashSet<>();
            Class<?> clazz = Class.forName(className);
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                String permission = getPermission(method.getModifiers());
                if (!PRIVATE.equals(permission)) {
                    MethodEntity methodEntity = new MethodEntity();
                    methodEntity.setPermission(getPermission(method.getModifiers()));
                    methodEntity.setReturnType(method.getReturnType().getName());
                    methodEntity.setName(method.getName());
                    methodEntity.setParameterTypes(getParameterTypes(method.getParameterTypes()));
                    methodEntity.setAnnotations(getAnnotation(method));
                    methodEntitySet.add(methodEntity);
                }
            }
            return methodEntitySet;
        } catch (ClassNotFoundException e) {
            throw new BusinessException(HintEnum.HINT_1032, e);
        }
    }

    /**
     * 根据类名获取类类型
     *
     * @author kuangq
     * @date 2019-12-13 10:38
     */
    private static Class<?> getClass(String className) {
        try {
            switch (className) {
                case BYTE:
                    return byte.class;
                case SHORT:
                    return short.class;
                case INT:
                    return int.class;
                case LONG:
                    return long.class;
                case FLOAT:
                    return float.class;
                case DOUBLE:
                    return double.class;
                case CHAR:
                    return char.class;
                case BOOLEAN:
                    return boolean.class;
                default:
                    return Class.forName(className);
            }
        } catch (ClassNotFoundException e) {
            throw new BusinessException(HintEnum.HINT_1032, e);
        }
    }

    /**
     * String[]转类类型数组
     *
     * @author kuangq
     * @date 2019-12-13 10:38
     */
    private static Class<?>[] getClasses(String[] parameterTypes) {
        Class<?>[] classes = new Class<?>[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            classes[i] = getClass(parameterTypes[i]);
        }
        return classes;
    }

    /**
     * 方法的动态调用
     *
     * @author kuangq
     * @date 2019-12-13 10:38
     */
    public static ApiResult invoke(Map<String, Object> params) {
        try {
            //构建获取方法的入参
            String className = params.get("className").toString();
            List<?> uriVariableList = (List<?>) params.get("uriVariables");
            Object[] uriVariables = new Object[uriVariableList.size()];
            uriVariableList.toArray(uriVariables);
            MethodEntity methodEntity = mapToMethodEntity((Map<?, ?>) params.get("methodEntity"));
            //获取要调用方法的所在类的类类型
            Class<?> clazz = Class.forName(className);
            //调用方法入参的类类型
            Class<?>[] classes = getClasses(methodEntity.getParameterTypes());
            //获取调用方法的实例
            Method method = clazz.getDeclaredMethod(methodEntity.getName(), classes);
            //获取类的实例
            Object instance = clazz.newInstance();
            //方法调用
            Object data = method.invoke(instance, uriVariables);
            //返回结果
            return ApiResult.success(data);
        } catch (ClassNotFoundException e) {
            throw new BusinessException(HintEnum.HINT_1032, e);
        } catch (NoSuchMethodException e) {
            throw new BusinessException(HintEnum.HINT_1029, e);
        } catch (IllegalAccessException e) {
            throw new BusinessException(HintEnum.HINT_1030, e);
        } catch (InstantiationException e) {
            throw new BusinessException(HintEnum.HINT_1036, e);
        } catch (InvocationTargetException e) {
            throw new BusinessException(HintEnum.HINT_1031, e);
        }
    }

    /**
     * map转MethodEntity
     *
     * @author kuangq
     * @date 2019-12-13 10:39
     */
    public static MethodEntity mapToMethodEntity(Map<?, ?> params) {
        //List转String数组
        List<?> parameterTypeList = (List<?>) params.get("parameterTypes");
        String[] parameterTypes = new String[parameterTypeList.size()];
        parameterTypeList.toArray(parameterTypes);
        //构建methodEntity对象
        MethodEntity methodEntity = new MethodEntity();
        methodEntity.setPermission(params.get("permission").toString());
        methodEntity.setReturnType(params.get("returnType").toString());
        methodEntity.setName(params.get("name").toString());
        methodEntity.setParameterTypes(parameterTypes);
        return methodEntity;
    }
}