package com.loyer.modules.system.service.impl;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.entity.User;
import com.loyer.common.core.utils.document.FileUtil;
import com.loyer.common.datasource.entity.PageResult;
import com.loyer.common.datasource.utils.PageHelperUtil;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.dedicine.utils.Base64Util;
import com.loyer.common.dedicine.utils.GeneralUtil;
import com.loyer.common.security.annotation.PermissionAnnotation;
import com.loyer.common.security.utils.SecurityUtil;
import com.loyer.modules.system.entity.Attachment;
import com.loyer.modules.system.mapper.postgresql.AttachmentMapper;
import com.loyer.modules.system.service.AttachmentService;
import com.loyer.modules.system.utils.FtpClientUtil;
import com.loyer.modules.system.utils.ImageUtil;
import com.loyer.modules.system.utils.ZipUtil;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.*;

/**
 * 附件ServiceImpl
 *
 * @author kuangq
 * @date 2020-08-02 15:20
 */
@Service
public class AttachmentServiceImpl implements AttachmentService {

    //入参存储位置
    public static final String SOURCE = "source";
    //入参是否获取base64
    public static final String GET_BASE_64 = "getBase64";
    private final String[] filterContentTypes = {MediaType.APPLICATION_JSON_VALUE};
    @Resource
    private AttachmentMapper attachmentMapper;
    @Resource
    private FastFileStorageClient fastFileStorageClient;

    /**
     * 附件上传
     *
     * @author kuangq
     * @date 2019-11-15 16:55
     */
    @Override
    @SneakyThrows
    @Transactional(transactionManager = "dataSourceTransactionManager", rollbackFor = Exception.class)
    public ApiResult upload(HttpServletRequest httpServletRequest) {
        //创建附件对象
        List<Attachment> attachmentList = new ArrayList<>();
        //获取前端入参
        Map<String, Object> params = new HashMap<>(8);
        httpServletRequest.getParameterMap().forEach((key, value) -> params.put(key, value[0]));
        //获取上传文件
        Collection<Part> parts = httpServletRequest.getParts();
        //遍历文件信息
        for (Part part : parts) {
            //获取mime类型
            String contentType = part.getContentType();
            //过滤非文件类型
            if (contentType != null && !Arrays.asList(filterContentTypes).contains(contentType)) {
                //创建附件对象
                Attachment attachment = new Attachment();
                //设置媒体类型
                attachment.setMime(contentType);
                //设置文件名称
                attachment.setName(part.getSubmittedFileName());
                //获取文件流
                InputStream inputStream = part.getInputStream();
                //判断媒体类型：图片需要做压缩处理
                if (contentType.length() > 5 && "image".equals(contentType.substring(0, 5))) {
                    //设置压缩前大小
                    attachment.setRawSize(inputStream.available());
                    //获取压缩目标大小
                    int targetSize = params.containsKey("targetSize") ? Integer.parseInt(params.get("targetSize").toString()) : 0;
                    //递归压缩
                    inputStream = ImageUtil.compress(inputStream, targetSize);
                }
                //设置压缩后大小
                attachment.setSize(inputStream.available());
                //物理存储
                attachment = saveAttachment(inputStream, params, attachment);
                attachmentList.add(attachment);
            }
        }
        //便于前端处理 只上传了一个附件的不返回数组类型
        Object result = attachmentList.size() == 1 ? attachmentList.get(0) : attachmentList;
        return ApiResult.success(result);
    }

    /**
     * 附件物理存储
     *
     * @author kuangq
     * @date 2020-08-03 18:27
     */
    @Override
    @SneakyThrows
    public Attachment saveAttachment(InputStream inputStream, Map<String, Object> params, Attachment attachment) {
        //转换为字节
        byte[] byteArray = IOUtils.toByteArray(inputStream);
        //判断是否数据库保存base64
        boolean setBase64 = params.containsKey("setBase64") && SpecialCharsConst.TRUE.equals(params.get("setBase64").toString());
        //根据source判断是否需要物理存储
        if (params.containsKey(SOURCE) && StringUtils.isNotBlank(params.get(SOURCE).toString())) {
            //获取传入文件类型，据此设置保存目录，默认其他
            attachment.setType(params.containsKey("type") ? params.get("type").toString() : "other");
            String type = attachment.getType() + SpecialCharsConst.SLASH;
            //获取文件后缀
            String suffix = "." + FilenameUtils.getExtension(attachment.getName()).toLowerCase();
            //判断文件存储位置
            String source = params.get("source").toString();
            //toByteArray方法会关闭inputStream，需重新赋值
            inputStream = new ByteArrayInputStream(byteArray);
            //存储只当前服务器
            if ("0".equals(source)) {
                String filePath = type + GeneralUtil.getUuid() + suffix;
                String savePath = SystemConst.LOCAL_FILE_PATH + filePath;
                FileUtil.saveFile(byteArray, savePath);
                attachment.setPath(filePath);
                attachment.setSource(source);
            }
            //存储至FTP文件系统
            else if ("1".equals(source)) {
                //创建上传至文件服务的位置
                String serverFilePath = SystemConst.FTP_SERVER_PATH + type;
                //创建文件名称
                String fileName = GeneralUtil.getUuid() + suffix;
                //上传文件
                if (!FtpClientUtil.upload(serverFilePath, fileName, inputStream)) {
                    throw new BusinessException(HintEnum.HINT_1070);
                }
                String filePath = type + fileName;
                attachment.setPath(filePath);
                attachment.setSource(source);
            }
            //存储至FastDfs文件系统
            else if ("2".equals(source)) {
                //去掉.获取扩展名
                String fileExtName = suffix.length() > 1 ? suffix.substring(1) : suffix;
                StorePath storePath = fastFileStorageClient.uploadFile(inputStream, inputStream.available(), fileExtName, null);
                attachment.setPath(storePath.getFullPath());
                attachment.setSource(source);
            } else {
                throw new BusinessException(HintEnum.HINT_1080, source);
            }
            //设置主键
            attachment.setUuid(GeneralUtil.getUuid());
            //获取用户信息
            User user = SecurityUtil.getLoginUser().getUser();
            params.put("userId", user.getUuid());
            params.put("userName", user.getName());
            params.put("userDeptId", user.getDeptId());
            //设置创建人信息
            attachment.setParams(params);
            //判断是否数据库保存base64
            if (setBase64) {
                attachment.setBase64(Base64.getEncoder().encodeToString(byteArray));
            }
            //设置保存成功标示
            params.put("saveFlag", attachmentMapper.saveAttachment(attachment));
        }
        //判断是否需要返回base64
        if (params.containsKey(GET_BASE_64) && Boolean.parseBoolean(params.get(GET_BASE_64).toString())) {
            if (!setBase64) {
                attachment.setBase64(Base64.getEncoder().encodeToString(byteArray));
            }
        } else {
            attachment.setBase64(null);
        }
        //设置传入参数
        attachment.setParams(params);
        return attachment;
    }

    /**
     * 保存附件信息
     *
     * @author kuangq
     * @date 2020-08-02 15:20
     */
    @Override
    public ApiResult saveAttachment(Attachment attachment) {
        return ApiResult.success(attachmentMapper.saveAttachment(attachment));
    }

    /**
     * 查询附件信息
     *
     * @author kuangq
     * @date 2020-08-02 15:20
     */
    @Override
    @PermissionAnnotation
    public ApiResult selectAttachment(Attachment attachment) {
        //判断是否为分页查询
        if (PageHelperUtil.isPaging(attachment)) {
            String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
            PageResult<Attachment> pageResult = PageHelperUtil.pagingQuery(attachmentMapper, methodName, attachment);
            return ApiResult.success(pageResult);
        } else {
            return ApiResult.success(attachmentMapper.selectAttachment(attachment));
        }
    }

    /**
     * 删除附件信息
     *
     * @author kuangq
     * @date 2020-08-02 15:20
     */
    @Override
    public ApiResult deleteAttachment(List<Attachment> attachmentList) {
        //先做物理删除
        for (Attachment attachment : attachmentList) {
            //验证文件路径不能为空
            String path = attachment.getPath();
            if (StringUtils.isNotBlank(path)) {
                //判断文件存储位置
                String source = attachment.getSource();
                //当前服务器删除
                if ("0".equals(source)) {
                    String filePath = SystemConst.LOCAL_FILE_PATH + path;
                    FileUtil.deleteFile(filePath);
                }
                //FTPClient文件删除
                else if ("1".equals(source)) {
                    String filePath = SystemConst.FTP_SERVER_PATH + path;
                    FtpClientUtil.delete(filePath);
                }
                //FastDfs文件删除
                else if ("2".equals(source)) {
                    fastFileStorageClient.deleteFile(path);
                }
            }
        }
        return ApiResult.success(attachmentMapper.deleteAttachment(attachmentList));
    }

    /**
     * 附件下载
     *
     * @author kuangq
     * @date 2020-08-03 21:24
     */
    @Override
    @SneakyThrows
    public void download(HttpServletResponse httpServletResponse, List<Attachment> attachmentList) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
        for (Attachment attachment : attachmentList) {
            //确保文件路径不为空
            if (StringUtils.isBlank(attachment.getPath())) {
                continue;
            }
            String path = attachment.getPath();
            InputStream inputStream = null;
            //判断文件存储位置
            String source = attachment.getSource();
            //获取本地服务器文件
            if ("0".equals(source)) {
                String filePath = SystemConst.LOCAL_FILE_PATH + path;
                File file = new File(filePath);
                if (file.exists()) {
                    inputStream = new FileInputStream(file);
                }
            }
            //FTP文件下载
            else if ("1".equals(source)) {
                String serverFilePath = SystemConst.FTP_SERVER_PATH + path;
                inputStream = FtpClientUtil.download(serverFilePath);
            }
            //FastDfs文件下载
            else if ("2".equals(source)) {
                StorePath storePath = StorePath.parseFromUrl(attachment.getPath());
                byte[] bytes = fastFileStorageClient.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadByteArray());
                inputStream = new ByteArrayInputStream(bytes);
            }
            //压缩至zip
            if (inputStream != null && inputStream.available() > 0) {
                String entryName = attachment.getType() + SpecialCharsConst.SLASH + attachment.getName();
                ZipUtil.compress(zipOutputStream, entryName, inputStream);
            }
        }
        zipOutputStream.close();
        FileUtil.download(httpServletResponse, byteArrayOutputStream.toByteArray(), "附件.zip");
    }

    /**
     * 查询附件的BASE64
     *
     * @author kuangq
     * @date 2020-07-10 0:20
     */
    @Override
    public ApiResult getFileBase64(String source, String filePath) {
        String base64;
        switch (source) {
            case "0":
                filePath = SystemConst.LOCAL_FILE_PATH + filePath;
                base64 = FileUtil.toBase64(filePath);
                break;
            case "1":
                filePath = SystemConst.FTP_SERVER_PATH + filePath;
                InputStream inputStream = FtpClientUtil.download(filePath);
                base64 = FileUtil.toBase64(inputStream);
                break;
            case "2":
                StorePath storePath = StorePath.parseFromUrl(filePath);
                byte[] bytes = fastFileStorageClient.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadByteArray());
                base64 = Base64Util.encode(bytes);
                break;
            default:
                base64 = "";
        }
        return ApiResult.success(base64);
    }
}