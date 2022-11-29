package com.loyer.common.core.utils.common;

import com.loyer.common.dedicine.enums.HintEnum;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 参数处理工具类
 *
 * @author kuangq
 * @date 2020-12-16 10:17
 */
public class ParamsUtil {

    /**
     * 封装入参
     *
     * @author kuangq
     * @date 2019-08-09 17:21
     */
    public static String sealingParams(Map<String, Object> params) {
        Objects.requireNonNull(params, HintEnum.HINT_1014.getMsg());
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, Object> resultMap = new TreeMap<>(new MapKeyComparator());
        resultMap.putAll(params);
        resultMap.forEach((key, value) -> stringBuilder.append(key).append("=").append(value).append("&"));
        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }

    /**
     * list转array
     *
     * @author kuangq
     * @date 2020-09-14 22:50
     */
    public static String[] listToArray(List<String> list) {
        return list.toArray(new String[0]);
    }

    /**
     * 从请求中获取入参字符串
     *
     * @author kuangq
     * @date 2020-12-07 15:01
     */
    @SneakyThrows
    public static String getInArgs(HttpServletRequest httpServletRequest) {
        httpServletRequest.setCharacterEncoding(StandardCharsets.UTF_8.name());
        InputStream inputStream;
        StringBuilder stringBuilder = new StringBuilder();
        inputStream = httpServletRequest.getInputStream();
        String str;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        while ((str = bufferedReader.readLine()) != null) {
            stringBuilder.append(str);
        }
        bufferedReader.close();
        inputStream.close();
        return stringBuilder.toString();
    }

    /*自定义比较器类：根据Map的Key值*/
    public static class MapKeyComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }
}