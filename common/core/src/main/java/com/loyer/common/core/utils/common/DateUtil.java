package com.loyer.common.core.utils.common;

import com.loyer.common.core.enums.DatePattern;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author kuangq
 * @title DateUtil
 * @description 日期工具类
 * @date 2020-08-17 10:28
 */
public class DateUtil {

    /**
     * @param
     * @return java.lang.String
     * @author kuangq
     * @description 获取时间戳
     * @date 2019-12-09 20:58
     */
    public static String getTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * @param datePattern
     * @return java.lang.String
     * @author kuangq
     * @description 获取指定格式的时间戳
     * @date 2020-07-20 16:06
     */
    public static String getTimestamp(DatePattern datePattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern.getPattern());
        return simpleDateFormat.format(new Date());
    }

    /**
     * @param date
     * @param datePattern
     * @return java.lang.String
     * @author kuangq
     * @description 时间戳转字符串
     * @date 2020-07-20 16:06
     */
    public static String getTimestamp(long date, DatePattern datePattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern.getPattern());
        return simpleDateFormat.format(date);
    }

    /**
     * @param dateStr
     * @param datePattern
     * @return long
     * @author kuangq
     * @description 字符串转时间戳
     * @date 2020-12-07 22:14
     */
    @SneakyThrows
    public static long getTimestamp(String dateStr, DatePattern datePattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern.getPattern());
        Date date = simpleDateFormat.parse(dateStr);
        return date.getTime();
    }

    /**
     * @param hour
     * @param minute
     * @return long
     * @author kuangq
     * @description 获取指定时间戳
     * @date 2020-05-28 10:17
     */
    public static long getTimestamp(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * @param startTime
     * @return double
     * @author kuangq
     * @description 获取时间差，精确到三位小数，单位秒
     * @date 2020-05-20 23:01
     */
    public static double getTdoa(long startTime) {
        long diff = System.currentTimeMillis() - startTime;
        BigDecimal bigDecimal1 = new BigDecimal(String.valueOf(diff));
        BigDecimal bigDecimal2 = new BigDecimal(String.valueOf(1000));
        BigDecimal tdoa = bigDecimal1.divide(bigDecimal2, 3, RoundingMode.HALF_UP);
        return tdoa.doubleValue();
    }

    /**
     * @param startTime
     * @return java.lang.String
     * @author kuangq
     * @description 获取时间差字符串
     * @date 2020-08-17 10:46
     */
    public static String getTdoaStr(long startTime) {
        long nd = 1000 * 60 * 60 * 24;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        //获得两个时间的毫秒差
        long diff = System.currentTimeMillis() - startTime;
        //计算差多少天
        long day = diff / nd;
        //计算差多少小时
        long hour = diff % nd / nh;
        //计算差多少分钟
        long min = diff % nd % nh / nm;
        return day + "天" + hour + "小时" + min + "分钟";
    }
}
