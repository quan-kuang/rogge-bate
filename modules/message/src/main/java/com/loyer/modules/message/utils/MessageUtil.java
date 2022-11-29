package com.loyer.modules.message.utils;

import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.enums.RegExp;
import com.loyer.common.core.utils.common.CheckParamsUtil;
import com.loyer.common.core.utils.common.OracleUtil;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.modules.message.entity.MessageParams;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20190711.models.SendStatus;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

/**
 * 腾讯云短信工具类
 *
 * @author kuangq
 * @date 2020-07-15 22:32
 */
public class MessageUtil {

    //腾讯云账户密钥账户
    private static final String SECRET_ID = "AKIDqZeiY7VI1YomWGtIchOkbQ1BAvTxMiz1";
    //腾讯云账户密钥密码
    private static final String SECRET_KEY = "uc4HRWv2tUs0rel36sNhlZO5YlhuS7vT";
    //短信应用SDKAppID
    private static final String SMS_SDK_APPID = "1400230032";
    //短信签名内容
    private static final String SIGN = "罗格贝特";
    //模板ID
    private static final String TEMPLATE_ID = "837124";
    //手机号地区前缀
    private static final String PHONE_PREFIX = "+86";

    /**
     * 短信发送
     *
     * @author kuangq
     * @date 2021-02-19 13:42
     */
    @SneakyThrows
    public static ApiResult sendMessage(MessageParams messageParams) {
        //实例化认证对象，传入腾讯云账户密钥对 secretId 和 secretKey，密钥查询：https://console.cloud.tencent.com/cam/capi
        Credential credential = new Credential(SECRET_ID, SECRET_KEY);
        //实例化一个http选项，可选，无特殊需求时可以跳过
        HttpProfile httpProfile = new HttpProfile();
        //通常无需指定域名，但访问金融区的服务时必须手动指定域名
        httpProfile.setEndpoint("sms.tencentcloudapi.com");
        //SDK默认POST请求
        httpProfile.setReqMethod("POST");
        //SDK默认超时时间
        httpProfile.setConnTimeout(60);
        //实例化客户端配置对象
        ClientProfile clientProfile = new ClientProfile();
        //SDK默认HmacSHA256签名方式
        clientProfile.setSignMethod("HmacSHA256");
        clientProfile.setHttpProfile(httpProfile);
        //实例化一个请求对象
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        //短信应用SDKAppID
        String smsSdkAppId = OracleUtil.nvl(messageParams.getSmsSdkAppId(), SMS_SDK_APPID).toString();
        sendSmsRequest.setSmsSdkAppid(smsSdkAppId);
        //短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名
        String sign = OracleUtil.nvl(messageParams.getSign(), SIGN).toString();
        sendSmsRequest.setSign(sign);
        //模板ID: 必须填写已审核通过的模板ID
        String templateId = OracleUtil.nvl(messageParams.getTemplateId(), TEMPLATE_ID).toString();
        sendSmsRequest.setTemplateID(templateId);
        //下发手机号码，+[国家或地区码][手机号]，最多不要超过200个手机号
        String[] phoneNumbers = getPhoneNumbers(messageParams.getPhoneNumbers());
        sendSmsRequest.setPhoneNumberSet(phoneNumbers);
        //模板参数: 若无模板参数，则设置为空
        String[] templateParams = messageParams.getTemplateParams().split(SpecialCharsConst.AND);
        sendSmsRequest.setTemplateParamSet(templateParams);
        //实例化SmsClient，第二个参数是地域信息
        SmsClient smsClient = new SmsClient(credential, "ap-shanghai", clientProfile);
        //通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的，接口返回SendSmsResponse实例对象
        SendSmsResponse sendSmsResponse = smsClient.SendSms(sendSmsRequest);
        //校验发送状态
        SendStatus sendStatus = sendSmsResponse.getSendStatusSet()[0];
        if (sendStatus.getFee() == 1) {
            return ApiResult.success(sendStatus.getMessage());
        }
        return ApiResult.failure(sendStatus.getMessage(), sendSmsResponse);
    }

    /**
     * 获取手机号数组
     *
     * @author kuangq
     * @date 2021-02-19 10:46
     */
    private static String[] getPhoneNumbers(String phoneNumbers) {
        String[] phoneNumberAry = phoneNumbers.split(SpecialCharsConst.COMMA);
        if (!CheckParamsUtil.regularCheck(RegExp.PHONE, (Object[]) phoneNumberAry)) {
            throw new BusinessException("手机号格式错误", phoneNumberAry);
        }
        for (int i = 0; i < phoneNumberAry.length; i++) {
            String phoneNumber = phoneNumberAry[i];
            if (!phoneNumber.startsWith(PHONE_PREFIX)) {
                phoneNumberAry[i] = StringUtils.join(PHONE_PREFIX, phoneNumber);
            }
        }
        return phoneNumberAry;
    }
}