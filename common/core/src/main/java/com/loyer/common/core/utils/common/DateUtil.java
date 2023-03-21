package com.loyer.common.core.utils.common;

import com.loyer.common.core.enums.DatePattern;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类
 *
 * @author kuangq
 * @date 2020-08-17 10:28
 */
public class DateUtil {

    /**
     * 获取时间戳
     *
     * @author kuangq
     * @date 2019-12-09 20:58
     */
    public static String getTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 获取指定格式的时间戳
     *
     * @author kuangq
     * @date 2020-07-20 16:06
     */
    public static String getTimestamp(DatePattern datePattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern.getPattern());
        return simpleDateFormat.format(new Date());
    }

    /**
     * 时间戳转字符串
     *
     * @author kuangq
     * @date 2020-07-20 16:06
     */
    public static String getTimestamp(long date, DatePattern datePattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern.getPattern());
        return simpleDateFormat.format(date);
    }

    /**
     * 字符串转时间戳
     *
     * @author kuangq
     * @date 2020-12-07 22:14
     */
    @SneakyThrows
    public static long getTimestamp(String dateStr, DatePattern datePattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern.getPattern());
        Date date = simpleDateFormat.parse(dateStr);
        return date.getTime();
    }

    /**
     * 获取指定时间戳
     *
     * @author kuangq
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
     * 获取时间差，精确到三位小数，单位秒
     *
     * @author kuangq
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
     * 获取时间差字符串
     *
     * @author kuangq
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

    /**
     * 时间换算
     *
     * @author kuangq
     * @date 2022-12-01 18:08
     */
    public static String getStr(long diff) {
        if (diff < 0) {
            return "∞";
        }
        long nd = 60 * 60 * 24;
        long nh = 60 * 60;
        long nm = 60;
        long day = diff / nd;
        if (day > 0) {
            return day + " day";
        }
        long hour = diff % nd / nh;
        if (hour > 0) {
            return hour + " hour";
        }
        long min = diff % nd % nh / nm;
        if (min > 0) {
            return min + " min";
        }
        long sec = diff % nd % nh;
        if (sec > 0) {
            return sec + " sec";
        }
        return "-";
    }

    /**
     * 获取指定日期所在本周时间戳
     *
     * @author kuangq
     * @date 2023-03-21 10:16
     */
    public static List<Long> getWeekDate(String dateStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.YMD_1.getPattern());
        LocalDate currentDate = LocalDate.parse(dateStr, dateTimeFormatter);
        int daysUntilMonday = currentDate.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        if (daysUntilMonday < 0) {
            daysUntilMonday += 7;
        }
        LocalDate mondayDate = currentDate.minusDays(daysUntilMonday);
        List<Long> weekDateList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate localDate = mondayDate.plusDays(i);
            Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            weekDateList.add(instant.toEpochMilli());
        }
        return weekDateList;
    }

    /**
     * 获取最近几天的日期
     *
     * @author kuangq
     * @date 2023-03-21 13:00
     */
    public static List<String> getRecentDate(String dateStr, int num) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.YMD_1.getPattern());
        LocalDate currentDate = StringUtils.isBlank(dateStr) ? LocalDate.now() : LocalDate.parse(dateStr, dateTimeFormatter);
        List<String> recentDateList = new ArrayList<>();
        for (int i = num - 1; i > 0; i--) {
            LocalDate localDate = currentDate.minusDays(i);
            recentDateList.add(localDate.format(dateTimeFormatter));
        }
        recentDateList.add(currentDate.format(dateTimeFormatter));
        return recentDateList;
    }
}