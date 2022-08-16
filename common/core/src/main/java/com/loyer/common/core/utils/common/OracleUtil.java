package com.loyer.common.core.utils.common;

/**
 * @author kuangq
 * @title OracleUtil
 * @description 封装oracle数据库函数
 * @date 2020-12-16 10:10
 */
public class OracleUtil {

    /**
     * @param obj1
     * @param obj2
     * @return java.lang.Object
     * @author kuangq
     * @description 封装Oracle/nvl函数
     * @date 2019-10-09 15:34
     */
    public static Object nvl(Object obj1, Object obj2) {
        return obj1 == null || "".equals(obj1) ? obj2 : obj1;
    }

    /**
     * @param obj1
     * @param obj2
     * @param obj3
     * @return java.lang.Object
     * @author kuangq
     * @description 封装Oracle/nvl2函数
     * @date 2019-10-09 15:35
     */
    public static Object nvl2(Object obj1, Object obj2, Object obj3) {
        return obj1 == null || "".equals(obj1) ? obj3 : obj2;
    }

    /**
     * @param objAry
     * @return java.lang.Object
     * @author kuangq
     * @description 封装Oracle/decode函数默认返回null
     * @date 2019-10-09 15:34
     */
    public static Object decode(Object... objAry) {
        int length = objAry.length;
        int count = (length & 1) == 0 ? length / 2 - 1 : length / 2;
        for (int i = 0; i < count; i++) {
            if (objAry[0].getClass() == objAry[2 * i + 1].getClass() && objAry[0].equals(objAry[2 * i + 1])) {
                return objAry[2 * i + 2];
            }
        }
        return (length % 2) == 0 ? objAry[length - 1] : null;
    }
}
