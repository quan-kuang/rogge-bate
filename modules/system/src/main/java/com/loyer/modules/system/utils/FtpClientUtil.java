package com.loyer.modules.system.utils;

import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.modules.system.entity.VsFtpd;
import lombok.SneakyThrows;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 文件服务器上传下载
 *
 * @author kuangq
 * @date 2020-05-27 15:59
 */
public class FtpClientUtil {

    private static final Logger logger = LoggerFactory.getLogger(FtpClientUtil.class);

    private static final VsFtpd VS_FTPD = ContextUtil.getBean(VsFtpd.class);

    /**
     * 初始化vsFtpd服务器链接
     *
     * @author kuangq
     * @date 2020-05-26 13:05
     */
    private static FTPClient getFtpClient() throws IOException {
        FTPClient ftpClient = new FTPClient();
        //设置连接超时时间
        ftpClient.setConnectTimeout(VS_FTPD.getTimeout());
        //设置字符集
        ftpClient.setControlEncoding(VS_FTPD.getUnicode());
        //链接
        ftpClient.connect(VS_FTPD.getUrl(), VS_FTPD.getPort());
        //登录
        ftpClient.login(VS_FTPD.getUsername(), VS_FTPD.getPassword());
        //判断响应状态
        int replyCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            ftpClient.disconnect();
        }
        //返回初始化对象
        return ftpClient;
    }

    /**
     * 上传文件
     *
     * @author kuangq
     * @date 2020-05-26 14:25
     */
    @SneakyThrows
    public static boolean upload(String localFilePath, String serverFilePath) {
        //判断需要上传的文件是否存在
        File file = new File(localFilePath);
        if (!file.exists()) {
            logger.error("【{}文件不存在】", localFilePath);
            return false;
        }
        InputStream inputStream = new FileInputStream(file);
        return upload(serverFilePath, file.getName(), inputStream);
    }

    /**
     * 上传文件
     *
     * @author kuangq
     * @date 2020-08-03 23:34
     */
    @SneakyThrows
    public static boolean upload(String serverFilePath, String fileName, InputStream inputStream) {
        FTPClient ftpClient = getFtpClient();
        try {
            //上传目录不存在则创建
            makeDirectory(ftpClient, serverFilePath);
            //进入到文件保存的目录
            ftpClient.changeWorkingDirectory(serverFilePath);
            //设置文件传输模式为主动模式
            ftpClient.enterLocalActiveMode();
            //设置文件类型为二进制，保证传输的内容不会被改变
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //保存文件
            boolean flag = ftpClient.storeFile(fileName, inputStream);
            logger.info("【{}文件上传：{}】", fileName, flag);
            return flag;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                }
            } catch (IOException e) {
                logger.error("【{}】{}", HintEnum.HINT_1011.getMsg(), e.getMessage());
            }
        }
    }

    /**
     * 取回远程文件
     *
     * @author kuangq
     * @date 2020-10-16 18:25
     */
    @SneakyThrows
    public static InputStream download(String serverFilePath) {
        FTPClient ftpClient = getFtpClient();
        try {
            InputStream inputStream = ftpClient.retrieveFileStream(serverFilePath);
            if (inputStream == null) {
                throw new BusinessException(HintEnum.HINT_1069, serverFilePath);
            }
            return inputStream;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                }
            } catch (IOException e) {
                logger.error("【{}】{}", HintEnum.HINT_1011.getMsg(), e.getMessage());
            }
        }
    }

    /**
     * 取回远程文件
     *
     * @author kuangq
     * @date 2020-10-16 19:12
     */
    @SneakyThrows
    public static boolean download(String serverFilePath, String localFilePath) {
        FTPClient ftpClient = getFtpClient();
        OutputStream outputStream = null;
        try {
            if (!localFilePath.contains(SpecialCharsConst.PERIOD)) {
                int index = serverFilePath.lastIndexOf(SpecialCharsConst.SLASH);
                localFilePath += serverFilePath.substring(index);
            }
            outputStream = new FileOutputStream(localFilePath);
            return ftpClient.retrieveFile(serverFilePath, outputStream);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                }
            } catch (IOException e) {
                logger.error("【{}】{}", HintEnum.HINT_1011.getMsg(), e.getMessage());
            }
        }
    }

    /**
     * 删除文件
     *
     * @author kuangq
     * @date 2020-08-04 0:10
     */
    @SneakyThrows
    public static boolean delete(String serverFilePath) {
        FTPClient ftpClient = getFtpClient();
        try {
            return ftpClient.deleteFile(serverFilePath);
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                }
            } catch (IOException e) {
                logger.error("【{}】{}", HintEnum.HINT_1011.getMsg(), e.getMessage());
            }
        }
    }

    /**
     * 创建文件夹
     *
     * @author kuangq
     * @date 2020-05-26 14:25
     */
    @SneakyThrows
    private static void makeDirectory(FTPClient ftpClient, String serverFilePath) {
        StringBuilder stringBuilder = new StringBuilder();
        //判断上传目录是否存在
        if (!ftpClient.changeWorkingDirectory(serverFilePath)) {
            //避免遍历到根目录，剔除最前面的字符/
            if (SpecialCharsConst.SLASH.equals(serverFilePath.substring(0, 1))) {
                serverFilePath = serverFilePath.substring(1);
            }
            //遍历路径层级
            for (String string : serverFilePath.split(SpecialCharsConst.SLASH)) {
                stringBuilder.append(SpecialCharsConst.SLASH);
                stringBuilder.append(string);
                //当前目录不存在
                if (!ftpClient.changeWorkingDirectory(stringBuilder.toString())) {
                    //转入上级目录
                    ftpClient.changeWorkingDirectory(stringBuilder.substring(0, stringBuilder.lastIndexOf(SpecialCharsConst.SLASH)));
                    //创建下级目录
                    ftpClient.makeDirectory(string);
                }
            }
        }
    }
}