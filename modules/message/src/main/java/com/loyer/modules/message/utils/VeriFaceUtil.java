package com.loyer.modules.message.utils;

import com.alibaba.fastjson.JSONObject;
import com.loyer.common.core.constant.PrefixConst;
import com.loyer.common.core.utils.document.FileUtil;
import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.common.core.utils.request.HttpUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.utils.Base64Util;
import com.loyer.common.redis.utils.RedisUtil;
import com.loyer.modules.message.entity.BaiduCloudKeyPair;
import com.loyer.modules.message.entity.BaiduEntity;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author kuangq
 * @title VeriFaceUtil
 * @description 百度人脸识别
 * @date 2020-10-22 14:47
 */
public class VeriFaceUtil {

    private static final RedisUtil REDIS_UTIL = ContextUtil.getBean(RedisUtil.class);

    //获取access_token的接口地址
    private static final String GET_TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    //人脸检测接口地址
    private static final String FACE_DETECT_URL = "https://aip.baidubce.com/rest/2.0/face/v3/detect?access_token=%s";
    //在线图片活体检测接口地址
    private static final String FACE_VERIFY_URL = "https://aip.baidubce.com/rest/2.0/face/v3/faceverify?access_token=%s";
    //身份证OCR接口地址
    private static final String OCR_ID_CARD_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard?access_token=%s";

    /**
     * @param keyPair
     * @return java.lang.String
     * @author kuangq
     * @description 获取access_token
     * @date 2020-10-22 20:24
     */
    private static String getAccessToken(BaiduCloudKeyPair keyPair) {
        //缓存key值
        String key = PrefixConst.BAIDU_AUTH_TOKEN + keyPair.getAppId();
        //校验缓存值是否存在
        if (REDIS_UTIL.isExist(key)) {
            return REDIS_UTIL.getValue(key);
        }
        //组装入参
        BaiduEntity.OauthRequest oauthRequest = new BaiduEntity.OauthRequest();
        oauthRequest.setGrant_type("client_credentials");
        oauthRequest.setClient_id(keyPair.getClientId());
        oauthRequest.setClient_secret(keyPair.getClientSecret());
        //发起请求
        BaiduEntity.OauthResponse oauthResponse = HttpUtil.doGet(GET_TOKEN_URL, oauthRequest, BaiduEntity.OauthResponse.class);
        String accessToken = oauthResponse.getAccess_token();
        //缓存access_token
        REDIS_UTIL.setValue(key, accessToken, 3600 * 24 * 20L);
        return accessToken;
    }

    /**
     * @param image
     * @param imageType
     * @param faceField
     * @return com.loyer.system.common.entity.ApiResult
     * @author kuangq
     * @description 人脸检测（主要检测人脸遮挡程度，保证有且仅有一张人脸）
     * @date 2021-02-19 11:15
     */
    @SneakyThrows
    public static ApiResult faceDetect(String image, String imageType, String faceField) {
        //接口地址拼接access_token
        String url = String.format(FACE_DETECT_URL, getAccessToken(WeightRobinUtil.round(WeightRobinUtil.AUTH_KEY_PAIR_MAP)));
        //组装认证数据
        BaiduEntity.FaceDetectRequest faceDetectRequest = new BaiduEntity.FaceDetectRequest();
        faceDetectRequest.setImage(image);
        faceDetectRequest.setImage_type(imageType);
        faceDetectRequest.setFace_field(faceField);
        //检测图片中面积最大的几张人脸
        faceDetectRequest.setMax_face_num(3);
        //发起请求
        String resultStr = HttpUtil.doPostJson(url, faceDetectRequest, String.class);
        //出参解析，字段未添加注释的，详情见文档https://ai.baidu.com/ai-doc/FACE/yk37c1u4t
        BaiduEntity.FaceDetectResponse faceDetectResponse = JSONObject.parseObject(resultStr, BaiduEntity.FaceDetectResponse.class);
        //校验请求结果
        if (faceDetectResponse.getError_code() == 0) {
            //获取人脸区域坐标点
            BaiduEntity.FaceDetectResponse.Result.Location location = faceDetectResponse.getResult().getFace_list().get(0).getLocation();
            //将图片的url转换成BASE64
            if ("URL".equalsIgnoreCase(imageType)) {
                URL imageUrl = new URL(image);
                image = FileUtil.toBase64(imageUrl.openStream());
            }
            //截取人脸区域照片
            String faceArea = clip(image, "jpg", location.getLeft().intValue(), location.getTop().intValue(), location.getWidth(), location.getHeight());
            faceDetectResponse.setFaceArea(faceArea);
            return ApiResult.success(faceDetectResponse);
        }
        return ApiResult.hintEnum(HintEnum.HINT_1083, faceDetectResponse.getError_msg());
    }

    /**
     * @param image     图片信息
     * @param imageType 图片类型
     * @param faceField 返回字段
     * @return com.loyer.system.common.entity.ApiResult
     * @author kuangq
     * @description 在线图片活体检测
     * @date 2020-10-22 20:36
     */
    public static ApiResult faceVerify(String image, String imageType, String faceField) {
        //接口地址拼接access_token
        String url = String.format(FACE_VERIFY_URL, getAccessToken(WeightRobinUtil.round(WeightRobinUtil.AUTH_KEY_PAIR_MAP)));
        //组装认证数据
        BaiduEntity.FaceVerifyRequest faceVerifyRequest = new BaiduEntity.FaceVerifyRequest();
        faceVerifyRequest.setImage(image);
        faceVerifyRequest.setImage_type(imageType);
        faceVerifyRequest.setFace_field(faceField);
        faceVerifyRequest.setOption("COMMON");
        //组装入参，数据类型为list
        List<BaiduEntity.FaceVerifyRequest> authParamsList = new ArrayList<BaiduEntity.FaceVerifyRequest>() {{
            add(faceVerifyRequest);
        }};
        //发起请求
        String resultStr = HttpUtil.doPostJson(url, authParamsList, String.class);
        //先用字符串接收在经过JSON序列号处理frr_1e-2特殊字段映射
        BaiduEntity.FaceVerifyResponse faceVerifyResponse = JSONObject.parseObject(resultStr, BaiduEntity.FaceVerifyResponse.class);
        //校验请求结果
        if (faceVerifyResponse.getError_code() == 0) {
            return ApiResult.success(faceVerifyResponse);
        }
        return ApiResult.hintEnum(HintEnum.HINT_1083, faceVerifyResponse.getError_msg());
    }

    /**
     * @param ocrIdCardRequest
     * @return com.loyer.common.dedicine.entity.ApiResult
     * @author kuangq
     * @description 身份证OCR识别
     * @date 2021-03-15 15:04
     */
    public static ApiResult ocrIdCard(BaiduEntity.OcrIdCardRequest ocrIdCardRequest) {
        //接口地址拼接access_token
        String url = String.format(OCR_ID_CARD_URL, getAccessToken(WeightRobinUtil.round(WeightRobinUtil.OCR_KEY_PAIR_MAP)));
        //组装认证数据，如果是URL类型，将image参数复制到url字段上，image置空
        if ("URL".equalsIgnoreCase(ocrIdCardRequest.getImageType())) {
            ocrIdCardRequest.setUrl(ocrIdCardRequest.getImage());
            ocrIdCardRequest.setImage(null);
        }
        //发起请求
        String resultStr = HttpUtil.doPostForm(url, ocrIdCardRequest, String.class);
        //先用字符串接收在经过JSON序列号处理特殊中文字段映射
        BaiduEntity.OcrIdCardResponse ocrIdCardResponse = JSONObject.parseObject(resultStr, BaiduEntity.OcrIdCardResponse.class);
        //校验请求结果
        if (ocrIdCardResponse.getWords_result_num() > 0) {
            return ApiResult.success(ocrIdCardResponse);
        }
        //返回失败结果
        return ApiResult.failure(ocrIdCardResponse.getImage_status(), ocrIdCardResponse);
    }

    /**
     * @param base64
     * @param suffix
     * @param left
     * @param top
     * @param width
     * @param height
     * @return java.lang.String
     * @author kuangq
     * @description 裁剪指定区域图片
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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, suffix, byteArrayOutputStream);
        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }
}