package com.loyer.modules.system.utils;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.loyer.common.core.constant.PrefixConst;
import com.loyer.common.core.entity.Captcha;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.utils.GeneralUtil;
import com.loyer.common.redis.utils.CacheUtil;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.util.Properties;

/**
 * 文字验证码工具类
 *
 * @author kuangq
 * @date 2019-12-30 9:51
 */
@SuppressWarnings({"AlibabaCommentsMustBeJavadocFormat", "FieldCanBeLocal"})
@Component
public class CodeCaptchaUtil {

    //图片边框，合法值：yes , no
    private final String border = "no";
    //边框颜色，合法值：r,g,b或者 white,black,blue.
    private final String borderColor = "black";
    //边框厚度，合法值：>0
    private final String borderThickness = "1";
    //图片宽度
    private final String imageWidth = "100";
    //图片高度
    private final String imageHeight = "40";
    //图片实现类
    private final String producerImpl = "com.google.code.kaptcha.impl.DefaultKaptcha";
    //文本实现类
    private final String textproducerImpl = "com.google.code.kaptcha.text.impl.DefaultTextCreator";
    //文本集合，验证码值从此集合中获取
    private final String charString = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    //验证码长度
    private final String charLength = "4";
    //字体
    private final String fontNames = "Arial, Courier";
    //字体大小
    private final String fontSize = "36";
    //字体颜色，合法值：r,g,b  或者 white,black,blue
    private final String fontColor = "131,139,139";
    //文字间隔
    private final String charSpace = "1";
    //干扰实现类
    private final String noiseImpl = "com.google.code.kaptcha.impl.DefaultNoise";
    //干扰颜色，合法值：r,g,b 或者 white,black,blue.
    private final String noiseColor = "131,139,139";
    //图片样式：水纹：WaterRipple、鱼眼：FishEyeGimpy、阴影：ShadowGimpy
    private final String obscurificatorImpl = "com.google.code.kaptcha.impl.WaterRipple";
    //背景实现类
    private final String backgroundImpl = "com.google.code.kaptcha.impl.DefaultBackground";
    //背景颜色渐变，开始颜色
    private final String clearFrom = "224,238,238";
    //背景颜色渐变，结束颜色
    private final String clearTo = "193,205,205";
    //文字渲染器
    private final String wordImpl = "com.google.code.kaptcha.text.impl.DefaultWordRenderer";

    @Resource
    private DefaultKaptcha defaultKaptcha;

    /**
     * 构建defaultKaptcha
     *
     * @author kuangq
     * @date 2019-12-30 23:26
     */
    @Lazy
    @Bean(name = "defaultKaptcha")
    public DefaultKaptcha defaultKaptcha() {
        Properties properties = new Properties();
        //图片边框，合法值：yes , no
        properties.setProperty("kaptcha.border", border);
        //边框颜色，合法值：r,g,b或者 white,black,blue.
        properties.setProperty("kaptcha.border.color", borderColor);
        //边框厚度，合法值：>0
        properties.setProperty("kaptcha.border.thickness", borderThickness);
        //图片宽度
        properties.setProperty("kaptcha.image.width", imageWidth);
        //图片高度
        properties.setProperty("kaptcha.image.height", imageHeight);
        //图片实现类
        properties.setProperty("kaptcha.producer.impl", producerImpl);
        //文本实现类
        properties.setProperty("kaptcha.textproducer.impl", textproducerImpl);
        //文本集合，验证码值从此集合中获取
        properties.setProperty("kaptcha.textproducer.char.string", charString);
        //验证码长度
        properties.setProperty("kaptcha.textproducer.char.length", charLength);
        //字体
        properties.setProperty("kaptcha.textproducer.font.names", fontNames);
        //字体大小
        properties.setProperty("kaptcha.textproducer.font.size", fontSize);
        //字体颜色，合法值：r,g,b  或者 white,black,blue
        properties.setProperty("kaptcha.textproducer.font.color", fontColor);
        //文字间隔
        properties.setProperty("kaptcha.textproducer.char.space", charSpace);
        //干扰实现类
        properties.setProperty("kaptcha.noise.impl", noiseImpl);
        //干扰颜色，合法值：r,g,b 或者 white,black,blue
        properties.setProperty("kaptcha.noise.color", noiseColor);
        //图片样式：水纹：WaterRipple、鱼眼：FishEyeGimpy、阴影：ShadowGimpy
        properties.setProperty("kaptcha.obscurificator.impl", obscurificatorImpl);
        //背景实现类
        properties.setProperty("kaptcha.background.impl", backgroundImpl);
        //背景颜色渐变，开始颜色
        properties.setProperty("kaptcha.background.clear.from", clearFrom);
        //背景颜色渐变，结束颜色
        properties.setProperty("kaptcha.background.clear.to", clearTo);
        //文字渲染器
        properties.setProperty("kaptcha.word.impl", wordImpl);
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    /**
     * 获取验证码的Base64
     *
     * @author kuangq
     * @date 2019-12-30 23:26
     */
    @SneakyThrows
    public ApiResult getCaptcha() {
        String nonceStr = GeneralUtil.getUuid();
        String value = nonceStr.substring(0, 4);
        CacheUtil.STRING.set(PrefixConst.CAPTCHA + nonceStr, value, 300);
        BufferedImage bufferedImage = defaultKaptcha.createImage(value);
        String base64 = ImageUtil.toBase64(bufferedImage, "jpg", true);
        Captcha captcha = new Captcha();
        captcha.setNonceStr(nonceStr);
        captcha.setCanvasSrc(base64);
        return ApiResult.success(captcha);
    }
}