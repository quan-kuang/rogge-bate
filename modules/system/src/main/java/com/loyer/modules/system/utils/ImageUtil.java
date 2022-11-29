package com.loyer.modules.system.utils;

import com.loyer.common.core.utils.document.FileUtil;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.dedicine.utils.Base64Util;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Base64;

/**
 * 图片工具类
 *
 * @author kuangq
 * @date 2020-06-25 15:49
 */
public class ImageUtil {

    //压缩质量
    private static final double SCALE = 0.77d;
    //字节大小1024
    private static final int BYTE_SIZE = 1024;

    /**
     * 压缩图片
     *
     * @author kuangq
     * @date 2021-03-06 13:52
     */
    public static String compress(String base64, int targetSize) {
        InputStream inputStream = FileUtil.toInputStream(base64);
        inputStream = compress(inputStream, targetSize);
        return FileUtil.toBase64(inputStream);
    }

    /**
     * 压缩图片
     *
     * @author kuangq
     * @date 2020-06-27 12:06
     */
    @SneakyThrows
    public static InputStream compress(InputStream inputStream, int targetSize) {
        //如果文件小于目标大小，进行压缩
        if (inputStream.available() < targetSize * BYTE_SIZE) {
            return inputStream;
        }
        //把图片读入到内存中
        BufferedImage bufferedImage = toBufferedImage(inputStream);
        //计算图片大小
        int[] measure = getMeasure(bufferedImage);
        //压缩图片并输出bufferedImage
        bufferedImage = Thumbnails.of(bufferedImage).size(measure[0], measure[1]).outputQuality(SCALE).asBufferedImage();
        //存储图片文件byte数组
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //图片写入到ImageOutputStream
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        //targetSize>0则递归压缩至目标大小
        if (targetSize > 0) {
            inputStream = compress(inputStream, targetSize);
        }
        return inputStream;
    }

    /**
     * 本地压缩
     *
     * @author kuangq
     * @date 2020-06-25 18:18
     */
    @SneakyThrows
    public static void localCompress(String filePath, int targetSize) {
        File file = new File(filePath);
        //判断文件是否存在
        if (!file.exists()) {
            throw new BusinessException(HintEnum.HINT_1009, filePath);
        }
        //如果文件小于目标大小，进行压缩
        if (file.length() <= (long) targetSize * BYTE_SIZE) {
            return;
        }
        //把图片读入到内存中
        BufferedImage bufferedImage = ImageIO.read(file);
        //计算图片大小
        int[] measure = getMeasure(bufferedImage);
        //压缩图片并输出file
        Thumbnails.of(filePath).size(measure[0], measure[1]).outputQuality(SCALE).toFile(filePath);
        //targetSize>0则递归压缩至目标大小
        if (targetSize > 0) {
            localCompress(filePath, targetSize);
        }
    }

    /**
     * 裁剪指定区域图片
     *
     * @author kuangq
     * @date 2021-02-20 15:18
     */
    @SneakyThrows
    public static String clip(String base64, String suffix, int left, int top, int width, int height) {
        //获取图片流
        byte[] bytes = Base64Util.decode(base64);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ImageInputStream imageInputStream = ImageIO.createImageInputStream(byteArrayInputStream);
        //创建ImageReader，解码的指定格式
        ImageReader imageReader = ImageIO.getImageReadersBySuffix(suffix).next();
        //设置读取源并设置true标记只向前搜索
        imageReader.setInput(imageInputStream, true);
        //设置图片裁剪区域
        Rectangle rectangle = new Rectangle(left, top, width, height);
        //描述如何对流进行解码的类
        ImageReadParam imageReadParam = imageReader.getDefaultReadParam();
        imageReadParam.setSourceRegion(rectangle);
        //提供BufferedImage，将其用作解码像素数据的目标
        BufferedImage bufferedImage = imageReader.read(0, imageReadParam);
        //返回无前缀的base64
        return toBase64(bufferedImage, "jpg", false);
    }

    /**
     * 获取图片宽高
     *
     * @author kuangq
     * @date 2020-06-25 18:18
     */
    private static int[] getMeasure(BufferedImage bufferedImage) {
        int width = new BigDecimal(bufferedImage.getWidth()).multiply(new BigDecimal(SCALE)).intValue();
        int height = new BigDecimal(bufferedImage.getHeight()).multiply(new BigDecimal(SCALE)).intValue();
        return new int[]{width, height};
    }

    /**
     * 通过Toolkit创建图片，处理ImageIO.read导致压缩图片变色问题
     *
     * @author kuangq
     * @date 2020-10-14 15:52
     */
    @SneakyThrows
    private static BufferedImage toBufferedImage(InputStream inputStream) {
        byte[] bytes = IOUtils.toByteArray(inputStream);
        Image image = Toolkit.getDefaultToolkit().createImage(bytes);
        BufferedImage bufferedImage = null;
        image = new ImageIcon(image).getImage();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
            GraphicsConfiguration graphicsDeviceDefaultConfiguration = graphicsDevice.getDefaultConfiguration();
            bufferedImage = graphicsDeviceDefaultConfiguration.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            //The system does not have a screen
        }
        if (bufferedImage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        Graphics graphics = bufferedImage.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return bufferedImage;
    }

    /**
     * BufferedImage转BASE64
     *
     * @author kuangq
     * @date 2020-11-14 9:41
     */
    @SneakyThrows
    public static String toBase64(BufferedImage bufferedImage, String type, boolean prefixing) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, type, byteArrayOutputStream);
        String base64 = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        return prefixing ? String.format("data:image/%s;base64,%s", type, base64) : base64;
    }
}