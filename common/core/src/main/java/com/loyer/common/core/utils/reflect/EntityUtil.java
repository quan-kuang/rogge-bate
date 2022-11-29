package com.loyer.common.core.utils.reflect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.SneakyThrows;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 实体类和MAP的相互转换
 *
 * @author kuangq
 * @date 2020-06-02 17:05
 */
@SuppressWarnings("unused")
public class EntityUtil {

    /**
     * 实体类/集合类型转Map
     *
     * @author kuangq
     * @date 2021-12-07 15:13
     */
    @SneakyThrows
    public static Map<String, Object> entityToMap(Object entity) {
        if (entity instanceof Map) {
            return JSONObject.parseObject(JSON.toJSONString(entity), new TypeReference<Map<String, Object>>() {
            });
        }
        Map<String, Object> map = new HashMap<>(32);
        //获取JavaBean的描述器
        BeanInfo beanInfo = Introspector.getBeanInfo(entity.getClass(), Object.class);
        //获取属性描述器
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        //对属性迭代
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            //属性名称
            String key = propertyDescriptor.getName();
            //反射获取get方法
            Method method = propertyDescriptor.getReadMethod();
            //用对象执行getter方法获得属性值
            Object value = method.invoke(entity);
            //把属性名-属性值 存到Map中
            map.put(key, value);
        }
        return map;
    }

    /**
     * Map转实体类
     *
     * @author kuangq
     * @date 2020-06-25 15:13
     */
    @SneakyThrows
    public static <T> T mapToEntity(Map<String, Object> map, Class<T> clazz) {
        //创建一个需要转换为的类型的对象
        T object = clazz.newInstance();
        //得到属性的描述器
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            //得到属性的setter方法
            Method setter = propertyDescriptor.getWriteMethod();
            //得到key名字和属性名字相同的value设置给属性
            setter.invoke(object, map.get(propertyDescriptor.getName()));
        }
        return object;
    }

    /**
     * 获取所有属性值，效果等同实体类转Map（保留属性名的大小写）
     *
     * @author kuangq
     * @date 2020-06-25 15:13
     */
    @SneakyThrows
    public static Map<String, Object> getFields(Object entity) {
        Map<String, Object> map = new HashMap<>(16);
        List<Field> fieldList = new ArrayList<>();
        //获取实体类的类类型
        Class<?> clazz = entity.getClass();
        //获取该类及其父类的所有字段
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        //遍历字段放入map对象
        for (Field field : fieldList) {
            field.setAccessible(true);
            //过滤null值
            if (field.get(entity) != null) {
                map.put(field.getName(), field.get(entity));
            }
        }
        return map;
    }
}