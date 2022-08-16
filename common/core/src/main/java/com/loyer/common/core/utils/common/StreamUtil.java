package com.loyer.common.core.utils.common;

import java.io.PrintWriter;

/**
 * @author kuangq
 * @title StreamUtil
 * @description 日志打印
 * @date 2021-05-26 15:46
 */
public class StreamUtil {

    /**
     * @param printWriter
     * @param message
     * @return void
     * @author kuangq
     * @description 打印方法
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