package com.loyer.modules.message.utils;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.modules.message.entity.MailParams;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * 邮件工具类
 *
 * @author kuangq
 * @date 2019-12-27 14:50
 */
@Component
public class MailUtil {

    @Value("${spring.mail.username}")
    private String userName;

    @Resource
    private JavaMailSender javaMailSender;

    /**
     * 发送邮件
     *
     * @author kuangq
     * @date 2021-05-30 14:15
     */
    @SneakyThrows
    public ApiResult sendMail(MailParams mailParams) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //true代表是multipart类型
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        //发件人
        mimeMessageHelper.setFrom(userName, mailParams.getSender());
        //收件人
        mimeMessageHelper.setTo(mailParams.getReceiver());
        //抄送人
        String carbonCopy = mailParams.getCarbonCopy();
        if (StringUtils.isNotBlank(carbonCopy)) {
            mimeMessageHelper.setCc(carbonCopy);
        }
        //主题
        mimeMessageHelper.setSubject(mailParams.getSubject());
        //邮件内容，true设置支持html
        mimeMessageHelper.setText(mailParams.getBody(), true);
        //文件资源
        List<MailParams.FileResource> fileResourceList = mailParams.getFileResourceList();
        if (fileResourceList != null) {
            for (MailParams.FileResource fileResource : fileResourceList) {
                InputStreamSource inputStreamSource = getInputSource(fileResource);
                if (inputStreamSource == null) {
                    continue;
                }
                //获取文件名字和插入位置
                String place = fileResource.getPlace();
                String name = fileResource.getName();
                //插入行默认类型为图片
                if ("0".equals(place)) {
                    mimeMessageHelper.addInline(name, inputStreamSource, ContentType.IMAGE_JPEG.getMimeType());
                }
                //插入附件
                else if ("1".equals(place)) {
                    mimeMessageHelper.addAttachment(name, inputStreamSource);
                }
            }
        }
        //发送邮件
        javaMailSender.send(mimeMessage);
        return ApiResult.success();
    }

    /**
     * 将附件资源转换成Resource
     *
     * @author kuangq
     * @date 2021-05-30 14:20
     */
    private InputStreamSource getInputSource(MailParams.FileResource fileResource) throws IOException {
        byte[] bytes;
        if ("URL".equalsIgnoreCase(fileResource.getType())) {
            URL url = new URL(fileResource.getContent());
            bytes = IOUtils.toByteArray(url.openStream());
        } else if ("BASE64".equalsIgnoreCase(fileResource.getType())) {
            bytes = DatatypeConverter.parseBase64Binary(fileResource.getContent());
        } else {
            return null;
        }
        return new ByteArrayResource(bytes);
    }
}