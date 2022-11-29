package com.loyer.common.dedicine.utils;

import com.loyer.common.dedicine.constant.SystemConst;

import java.util.Random;
import java.util.UUID;

/**
 * 通用工具类
 *
 * @author kuangq
 * @date 2019-09-04 21:36
 */
public class GeneralUtil {

    /**
     * 获取UUID
     *
     * @author kuangq
     * @date 2019-09-04 21:58
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 获取当前操作系统
     *
     * @author kuangq
     * @date 2020-07-01 23:45
     */
    public static String getOs() {
        String operateSystem = System.getProperty("os.name").toLowerCase();
        if (operateSystem.contains(SystemConst.WINDOWS)) {
            return SystemConst.WINDOWS;
        } else if (operateSystem.contains(SystemConst.LINUX)) {
            return SystemConst.LINUX;
        } else if (operateSystem.contains(SystemConst.MAC)) {
            return SystemConst.MAC;
        } else {
            return "other";
        }
    }

    /**
     * 线程等待
     *
     * @author kuangq
     * @date 2021-07-13 17:49
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    /**
     * 获取随机数
     *
     * @author kuangq
     * @date 2021-06-15 17:19
     */
    public static String getRandom(int digit) {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < digit; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }
}