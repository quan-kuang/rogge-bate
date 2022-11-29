package com.loyer.modules.system.utils;

import com.alibaba.fastjson.JSON;
import com.loyer.common.core.utils.common.ParamsUtil;
import com.loyer.common.core.utils.reflect.BeanUtil;
import com.loyer.common.core.utils.reflect.EntityUtil;
import com.loyer.common.core.utils.request.HttpsUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.utils.StringUtil;
import com.loyer.modules.system.entity.TencentEntity;
import lombok.SneakyThrows;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;

/**
 * 腾讯云慧眼实名核身接口
 *
 * @author kuangq
 * @date 2021-02-23 11:23
 */
public class AuthFaceUtil {

    //密匙ID
    private static final String SECRET_ID = "AKIDsVdeS96kQpxpSLuXkeQwC8opZ2A1cpmR";
    //密匙KEY
    private static final String SECRET_KEY = "bv2arsd8NOAg4B6k7C3dw5xZv0oPkohK";

    /**
     * 实名核身鉴权
     *
     * @author kuangq
     * @date 2021-02-23 15:01
     */
    public static ApiResult detectAuth(String ruleId, String redirectUrl) {
        //获取公共参数并复制给detectAuthRequest
        TencentEntity.DetectAuthRequest request = new TencentEntity.DetectAuthRequest();
        TencentEntity.CloudEyeBaseRequest cloudEyeBaseRequest = getBaseParams();
        BeanUtil.copyProperties(cloudEyeBaseRequest, request);
        //设置请求参数
        request.setAction("DetectAuth");
        request.setRuleId(ruleId);
        request.setRedirectUrl(redirectUrl);
        //接口调用
        return interfaceCall(request);
    }

    /**
     * 获取实名核身结果信息
     *
     * @author kuangq
     * @date 2021-02-23 15:01
     */
    public static ApiResult getDetectInfo(String ruleId, String infoType, String bizToken) {
        //获取公共参数并复制给detectAuthRequest
        TencentEntity.GetDetectInfoRequest request = new TencentEntity.GetDetectInfoRequest();
        TencentEntity.CloudEyeBaseRequest cloudEyeBaseRequest = getBaseParams();
        BeanUtil.copyProperties(cloudEyeBaseRequest, request);
        //设置请求参数
        request.setAction("GetDetectInfo");
        request.setRuleId(ruleId);
        request.setInfoType(infoType);
        request.setBizToken(bizToken);
        //接口调用
        return interfaceCall(request);
    }

    /**
     * 解析接口响应结果集
     *
     * @author kuangq
     * @date 2019-09-18 10:23
     */
    private static ApiResult interfaceCall(Object entity) {
        //将实体类转换为map，保留字段名大小写
        Map<String, Object> params = EntityUtil.getFields(entity);
        //设置加密签名
        params.put("Signature", getSignature(params));
        //拼接请求地址
        String requestUrl = getUrl(params);
        String responseStr = HttpsUtil.request(requestUrl, "GET", null, null);
        //解析出参
        TencentEntity.CloudEyeResponse.Response response = JSON.parseObject(responseStr, TencentEntity.CloudEyeResponse.class).getResponse();
        //请求异常
        if (response.getError() != null) {
            ApiResult.failure(response.getError().getMessage(), response.getRequestId());
        }
        return ApiResult.success(response);
    }

    /**
     * 获取公共参数
     *
     * @author kuangq
     * @date 2021-02-23 15:01
     */
    private static TencentEntity.CloudEyeBaseRequest getBaseParams() {
        TencentEntity.CloudEyeBaseRequest cloudEyeBaseRequest = new TencentEntity.CloudEyeBaseRequest();
        cloudEyeBaseRequest.setRegion("ap-chongqing");
        cloudEyeBaseRequest.setVersion("2018-03-01");
        cloudEyeBaseRequest.setNonce(new Random().nextInt(Integer.MAX_VALUE));
        cloudEyeBaseRequest.setTimestamp(System.currentTimeMillis() / 1000);
        cloudEyeBaseRequest.setSecretId(SECRET_ID);
        return cloudEyeBaseRequest;
    }

    /**
     * 获取验签
     *
     * @author kuangq
     * @date 2021-02-23 15:01
     */
    @SneakyThrows
    private static String getSignature(Map<String, Object> params) {
        //请求方法 + 请求主机 +请求路径 + ? + 请求字符串
        String srcStr = "GETfaceid.tencentcloudapi.com/?" + ParamsUtil.sealingParams(params);
        //加密生成签名
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKeySpec = new SecretKeySpec(StringUtil.decode(SECRET_KEY), mac.getAlgorithm());
        mac.init(secretKeySpec);
        byte[] bytes = mac.doFinal(StringUtil.decode(srcStr));
        return DatatypeConverter.printBase64Binary(bytes);
    }

    /**
     * 拼接请求的url
     *
     * @author kuangq
     * @date 2021-02-23 16:37
     */
    @SneakyThrows
    private static String getUrl(Map<String, Object> params) {
        StringBuilder url = new StringBuilder("https://faceid.tencentcloudapi.com/?");
        //需要对请求串进行urlencode，由于key都是英文字母，故此处仅对其value进行urlencode
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            url.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8.name())).append("&");
        }
        return url.substring(0, url.length() - 1);
    }
}