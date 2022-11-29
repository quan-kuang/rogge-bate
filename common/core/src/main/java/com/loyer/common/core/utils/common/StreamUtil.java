package com.loyer.common.core.utils.common;

import java.io.PrintWriter;

/**
 * 日志打印
 *
 * @author kuangq
 * @date 2021-05-26 15:46
 */
public class StreamUtil {

    /**
     * 打印方法
     *
     * @author kuangq
     * @date 2021-05-26 15:47
     */
    public static void println(PrintWriter printWriter, String message) {
        if (printWriter == null) {
            return;
        }
        System.out.println(message);
        printWriter.println(message);
        printWriter.flush();
    }
}