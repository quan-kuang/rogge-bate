package com.loyer.common.core.utils.document;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kuangq
 * @title JsonUtil
 * @description JSON工具类
 * @date 2020-02-13 10:05
 */
public class JsonUtil {

    private static final Pattern PATTERN = Pattern.compile("\\s*|\t|\r|\n");

    /**
     * @param filePath
     * @return java.lang.String
     * @author kuangq
     * @description 读取json文件
     * @date 2020-02-13 10:06
     */
    public static String readJsonFile(String filePath) {
        FileReader fileReader = null;
        Reader reader = null;
        try {
            File file = new File(filePath);
            fileReader = new FileReader(file);
            reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            int length;
            StringBuilder stringBuilder = new StringBuilder();
            while ((length = reader.read()) != -1) {
                stringBuilder.append((char) length);
            }
            fileReader.close();
            reader.close();
            return getStringNoBlank(stringBuilder.toString());
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                //异常：HintEnum.HINT_1011
            }
        }
    }

    /**
     * @param str
     * @return java.lang.String
     * @author kuangq
     * @description 剔除字符串中空格
     * @date 2020-02-13 10:07
     */
    public static String getStringNoBlank(String str) {
        if (str != null && !"".equals(str)) {
            Matcher matcher = PATTERN.matcher(str);
            return matcher.replaceAll("");
        } else {
            return str;
        }
    }
}