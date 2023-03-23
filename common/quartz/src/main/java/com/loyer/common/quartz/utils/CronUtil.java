package com.loyer.common.quartz.utils;

import com.loyer.common.core.enums.DatePattern;
import com.loyer.common.core.utils.common.DateUtil;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Corn表达式工具类：cron表达式生成地址：<a href="http://cron.qqe2.com/"></a>
 *
 * @author kuangq
 * @date 2020-12-16 17:13
 */
public class CronUtil {

    /**
     * 校验Cron表达式是否有效
     *
     * @author kuangq
     * @date 2020-12-16 17:15
     */
    public static boolean isValid(String expression) {
        return CronExpression.isValidExpression(expression);
    }

    /**
     * 返回表达式的无效说明，有效则返回NULL
     *
     * @author kuangq
     * @date 2020-12-16 17:17
     */
    public static String getInvalidMessage(String expression) {
        try {
            new CronExpression(expression);
            return null;
        } catch (ParseException e) {
            return e.getMessage();
        }
    }

    /**
     * 获取表达式最近几次的执行时间
     *
     * @author kuangq
     * @date 2021-09-18 13:55
     */
    public static List<String> getExecuteTime(String expression, int count) {
        List<String> strList = new ArrayList<>();
        Date date = new Date();
        for (int i = 0; i < count; i++) {
            date = getExecuteTime(expression, date);
            strList.add(DateUtil.getTimestamp(date.getTime(), DatePattern.YMD_HMS_1));
        }
        return strList;
    }

    /**
     * 获取表达式指定时间以后的执行时间
     *
     * @author kuangq
     * @date 2021-09-18 13:55
     */
    public static Date getExecuteTime(String expression, Date date) {
        try {
            CronExpression cronExpression = new CronExpression(expression);
            return cronExpression.getNextValidTimeAfter(date);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}