package com.loyer.common.core.utils.document;

import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.dedicine.utils.Base64Util;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * File工具类
 *
 * @author kuangq
 * @date 2019-10-10 10:29
 */
public class FileUtil {

    /**
     * 删除文件
     *
     * @author kuangq
     * @date 2020-05-12 16:44
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File item : Objects.requireNonNull(files)) {
                deleteFile(item.getPath());
            }
        }
        return file.delete();
    }

    /**
     * MultipartFile转Base64
     *
     * @author kuangq
     * @date 2020-08-24 11:17
     */
    @SneakyThrows
    public static String toBase64(MultipartFile multipartFile) {
        byte[] bytes = multipartFile.getBytes();
        return Base64Util.encode(bytes);
    }

    /**
     * File转Base64
     *
     * @author kuangq
     * @date 2020-08-24 11:36
     */
    @SneakyThrows
    public static String toBase64(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new BusinessException(HintEnum.HINT_1009, filePath);
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        return toBase64(fileInputStream);
    }

    /**
     * InputStream转Base64
     *
     * @author kuangq
     * @date 2020-06-27 12:03
     */
    @SneakyThrows
    public static String toBase64(InputStream inputStream) {
        byte[] bytes = IOUtils.toByteArray(inputStream);
        return Base64Util.encode(bytes);
    }

    /**
     * Base64转InputStream
     *
     * @author kuangq
     * @date 2021-03-06 13:50
     */
    public static InputStream toInputStream(String base64) {
        //转化为输入流
        return IOUtils.toInputStream(base64, StandardCharsets.UTF_8);
    }

    /**
     * File转String
     *
     * @author kuangq
     * @date 2020-07-01 22:36
     */
    @SneakyThrows
    public static String toString(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new BusinessException(HintEnum.HINT_1009, filePath);
        }
        if (!file.isFile()) {
            throw new BusinessException(HintEnum.HINT_1063, filePath);
        }
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    /**
     * base64转文件下载
     *
     * @author kuangq
     * @date 2020-05-12 10:10
     */
    @SneakyThrows
    public static void download(HttpServletResponse httpServletResponse, String base64, String fileName) {
        byte[] bytes = Base64Util.decode(base64);
        download(httpServletResponse, bytes, fileName);
    }

    /**
     * bytes转文件下载
     *
     * @author kuangq
     * @date 2020-07-20 22:42
     */
    @SneakyThrows
    public static void download(HttpServletResponse httpServletResponse, byte[] bytes, String name) {
        httpServletResponse.reset();
        httpServletResponse.setContentType("application/octet-stream; charset=UTF-8");
        httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "File-Name");
        httpServletResponse.addHeader("Content-Length", String.valueOf(bytes.length));
        httpServletResponse.setHeader("File-Name", URLEncoder.encode(name, StandardCharsets.UTF_8.name()));
        IOUtils.write(bytes, httpServletResponse.getOutputStream());
    }

    /**
     * 保存FIle到本地目录
     *
     * @author kuangq
     * @date 2020-05-12 13:19
     */
    @SneakyThrows
    public static void saveFile(byte[] bytes, String filePath) {
        BufferedInputStream bufferedInputStream = null;
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
            bufferedInputStream = new BufferedInputStream(byteInputStream);
            File file = new File(filePath);
            File path = file.getParentFile();
            if (!path.exists()) {
                path.mkdirs();
            }
            fileOutputStream = new FileOutputStream(file);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            byte[] buffer = new byte[1024];
            int length = bufferedInputStream.read(buffer);
            while (length != -1) {
                bufferedOutputStream.write(buffer, 0, length);
                length = bufferedInputStream.read(buffer);
            }
            bufferedOutputStream.flush();
        } finally {
            try {
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (Exception e) {
                //异常：HintEnum.HINT_1011
            }
        }
    }

    /**
     * 获取resources下资源文件
     *
     * @author kuangq
     * @date 2020-07-01 23:04
     */
    @SneakyThrows
    public static File getResourceFile(String filePath) {
        return ResourceUtils.getFile("classpath:" + filePath);
    }
}