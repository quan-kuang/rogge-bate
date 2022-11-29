package com.loyer.common.datasource.utils;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loyer.common.core.utils.common.StringUtil;
import com.loyer.common.core.utils.reflect.BeanUtil;
import com.loyer.common.datasource.entity.PageParams;
import com.loyer.common.datasource.entity.PageResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import lombok.SneakyThrows;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * PageHelper工具类
 *
 * @author kuangq
 * @date 2020-05-16 17:12
 */
public class PageHelperUtil {

    /**
     * 根据入参判断是否为分页查询
     *
     * @author kuangq
     * @date 2022-01-07 10:36
     */
    public static boolean isPaging(Object entity) {
        try {
            checkPaging(entity);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

    /**
     * 分页参数校验
     *
     * @author kuangq
     * @date 2022-01-07 10:37
     */
    public static PageParams checkPaging(Object object) {
        try {
            PageParams pageParams = JSON.parseObject(JSON.toJSONString(object), PageParams.class);
            List<Method> methodList = BeanUtil.getGetMethods(pageParams);
            for (Method method : methodList) {
                if (method.invoke(pageParams) == null && !Class.class.getName().equals(method.getReturnType().getName())) {
                    throw new BusinessException(String.format("%s不能为空", StringUtil.firstLower(method.getName().substring(3))), pageParams);
                }
            }
            return pageParams;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(HintEnum.HINT_1005, object);
        }
    }

    /**
     * 分页查询
     *
     * @author kuangq
     * @date 2020-06-02 17:44
     */
    @SneakyThrows
    public static <T> PageResult<T> pagingQuery(Object instance, String methodName, Object params) {
        PageParams pageParams = checkPaging(params);
        //分页查询
        PageHelper.startPage(pageParams.getPageNum(), pageParams.getPageSize());
        //获取经过代理的目标类名称
        String targetClassName = AopUtils.getTargetClass(instance).getGenericInterfaces()[0].getTypeName();
        //根据函数名过滤调用方法执行
        Method method = Arrays.stream(Class.forName(targetClassName).getMethods()).filter(item -> item.getName().equals(methodName)).findFirst().orElse(null);
        Object data = Objects.requireNonNull(method, String.format("函数未定义：%s:%s", targetClassName, methodName)).invoke(instance, params);
        //组装分页结果
        PageInfo<T> pageInfo = new PageInfo<T>((List) data);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }
}