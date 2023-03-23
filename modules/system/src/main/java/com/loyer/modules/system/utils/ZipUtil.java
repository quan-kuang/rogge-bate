package com.loyer.modules.system.utils;

import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.constant.SuffixConst;
import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.dedicine.enums.HintEnum;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Enumeration;

/**
 * Zip工具类
 *
 * @author kuangq
 * @date 2020-05-27 16:32
 */
public class ZipUtil {

    private static final Logger logger = LoggerFactory.getLogger(ZipUtil.class);

    /**
     * 压缩文件
     *
     * @author kuangq
     * @date 2020-05-28 16:26
     */
    @SneakyThrows
    public static int compress(String filePath, String zipName) {
        FileOutputStream fileOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        try {
            //记录压缩文件数/开始时间
            long startTime = System.currentTimeMillis();
            int count = 0;
            //判断传入的压缩目录是否存在
            File file = new File(filePath);
            if (!file.exists()) {
                logger.error("【{}不存在】", filePath);
                return count;
            }
            //未传入压缩至目录直接压缩到当前路径
            if (StringUtils.isBlank(zipName)) {
                if (file.isDirectory()) {
                    zipName = filePath;
                } else {
                    zipName = file.getPath().substring(0, file.getPath().lastIndexOf(SpecialCharsConst.PERIOD)) + SuffixConst.ZIP;
                }
            }
            //斜杠转义
            zipName = zipName.replace(SpecialCharsConst.SLASH, File.separator);
            //根据后缀名判断输入的是文件夹还是文件
            boolean isZipName = SuffixConst.ZIP.equals(zipName.substring(zipName.length() - 4));
            String dirsPath = isZipName ? zipName.substring(0, zipName.lastIndexOf(File.separator)) : zipName;
            //判断根目录是否存在
            File dirsFile = new File(dirsPath);
            if (!dirsFile.exists()) {
                dirsFile.mkdirs();
            }
            //非文件名添加后缀
            if (!file.isFile() && !isZipName) {
                //目录名称不规则的额外处理
                if (!File.separator.equals(zipName.substring(zipName.length() - 1))) {
                    zipName += File.separator;
                }
                String name = file.isFile() ? file.getName().substring(0, file.getName().lastIndexOf(".")) : file.getName();
                zipName += name + SuffixConst.ZIP;
            }
            //创建zip对象输出流
            fileOutputStream = new FileOutputStream(zipName);
            zipOutputStream = new ZipOutputStream(fileOutputStream);
            //处理文件名中文乱码
            zipOutputStream.setEncoding(StandardCharsets.UTF_8.name());
            //压缩
            count = compress(zipOutputStream, file, "", zipName, count);
            double costTime = DateUtil.getTdoa(startTime);
            logger.info("【文件压缩完成，完成{}个，耗时{}s】", count, costTime);
            return count;
        } finally {
            try {
                if (zipOutputStream != null) {
                    zipOutputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                logger.error("【{}】{}", HintEnum.HINT_1011.getMsg(), e.getMessage());
            }
        }
    }

    /**
     * 创建压缩目录
     *
     * @author kuangq
     * @date 2020-05-28 17:05
     */
    @SneakyThrows
    private static int compress(ZipOutputStream zipOutputStream, File zipFile, String directory, String zipName, int count) {
        //获取传入目录名称
        String dirName = (StringUtils.isNotBlank(directory) ? directory + SpecialCharsConst.SLASH : SpecialCharsConst.BLANK);
        //判断需要压缩的是文件夹还是文件
        if (zipFile.isDirectory()) {
            //获取目录下全部对象
            File[] files = zipFile.listFiles();
            //判断目录下是否为空
            if (files == null || files.length == 0) {
                //创建文件夹
                ZipEntry zipEntry = new ZipEntry(dirName);
                //linux赋权、处理中文乱码
                zipEntry.setUnixMode(777);
                zipOutputStream.putNextEntry(zipEntry);
                return count;
            }
            //遍历目录下所有文件对象
            for (File file : files) {
                //压缩至当前目录时避免把压缩包一起打包
                if (zipName.equals(file.getPath())) {
                    continue;
                }
                //文件压缩名称
                String entryName = dirName + file.getName();
                //判断是否为文件夹，是则递归压缩文件
                if (file.isDirectory()) {
                    count = compress(zipOutputStream, file, entryName, zipName, count);
                } else {
                    count += compress(zipOutputStream, entryName, Files.newInputStream(file.toPath()));
                }
            }
        } else {
            //文件直接压缩
            String entryName = dirName + zipFile.getName();
            count += compress(zipOutputStream, entryName, Files.newInputStream(zipFile.toPath()));
        }
        return count;
    }

    /**
     * 文件写入
     *
     * @author kuangq
     * @date 2020-05-28 16:26
     */
    @SneakyThrows
    public static int compress(ZipOutputStream zipOutputStream, String entryName, InputStream inputStream) {
        //创建压缩目录条
        ZipEntry zipEntry = new ZipEntry(entryName);
        //linux赋权、处理中文乱码
        zipEntry.setUnixMode(777);
        zipOutputStream.putNextEntry(zipEntry);
        //加入缓冲区优化写入效率
        byte[] bytes = new byte[2048];
        //写入文件
        int len;
        while ((len = inputStream.read(bytes)) > 0) {
            zipOutputStream.write(bytes, 0, len);
        }
        inputStream.close();
        return 1;
    }

    /**
     * 解压
     *
     * @author kuangq
     * @date 2020-05-28 16:27
     */
    @SneakyThrows
    public static int decompress(String zipFilePath, String unZipPath) {
        ZipFile zipFile = null;
        try {
            //记录解压文件数/开始时间
            long startTime = System.currentTimeMillis();
            int count = 0;
            //判断zip文件是否存在
            File rootFile = new File(zipFilePath);
            if (!rootFile.exists()) {
                logger.error("【{}文件不存在】", zipFilePath);
                return count;
            }
            //判断是否传入解压路径
            if (StringUtils.isBlank(unZipPath)) {
                //为空创建同名文件夹作为解压目录
                unZipPath = zipFilePath.substring(0, zipFilePath.lastIndexOf(SpecialCharsConst.PERIOD));
            }
            //判断根目录是否存在
            File dirsFile = new File(unZipPath);
            if (!dirsFile.exists()) {
                dirsFile.mkdirs();
            }
            //创建zip解压对象
            zipFile = new ZipFile(rootFile);
            for (Enumeration<?> enumeration = zipFile.getEntries(); enumeration.hasMoreElements(); ) {
                //遍历解压对象
                ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
                //获取解压文件路径
                String filePath = String.format("%s/%s", unZipPath, zipEntry.getName());
                //创建解压文件
                File file = new File(filePath);
                //判断解压对象是否为文件
                if (zipEntry.isDirectory()) {
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                } else {
                    //保证解压对象的根目录存在
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
                    //获取具体的ZipEntry的输入流
                    InputStream inputStream = zipFile.getInputStream(zipEntry);
                    //创建解压文件输出流
                    OutputStream outputStream = Files.newOutputStream(file.toPath());
                    //加入缓冲区优化写入效率
                    byte[] bytes = new byte[2048];
                    //写入文件
                    int len;
                    while ((len = inputStream.read(bytes)) > 0) {
                        outputStream.write(bytes, 0, len);
                    }
                    count++;
                    //关闭输入输出流
                    outputStream.close();
                    inputStream.close();
                }
            }
            double costTime = DateUtil.getTdoa(startTime);
            logger.info("【文件解压完成，完成{}个，耗时{}s】", count, costTime);
            return count;
        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (IOException e) {
                logger.error("【{}】{}", HintEnum.HINT_1011.getMsg(), e.getMessage());
            }
        }
    }
}