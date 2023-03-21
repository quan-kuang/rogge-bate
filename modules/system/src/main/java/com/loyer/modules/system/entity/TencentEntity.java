package com.loyer.modules.system.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 腾讯云API实体类
 *
 * @author kuangq
 * @date 2020-11-09 15:47
 */
@Data
public class TencentEntity {

    @Data
    @NoArgsConstructor
    public static class ErrorMsg {
        //异常代码
        private Integer errcode;
        //异常说明
        private String errmsg;
        //消息id
        private String msgid;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class AccessToken extends ErrorMsg {
        //安全凭证
        private String access_token;
        //超期时间
        private Integer expires_in;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class ApiTicket extends ErrorMsg {
        //安全凭证
        private String ticket;
        //过期时间
        private Integer expires_in;
    }

    @Data
    @NoArgsConstructor
    public static class JsSdk {
        //公众号的appSecret
        private String appId;
        //公众号的appSecret
        private String appSecret;
        //调用jssdk的路由地址
        private String url;
    }

    @Data
    @NoArgsConstructor
    public static class Authorize {
        //应用授权作用域，snsapi_base （不弹出授权页面，直接跳转，只能获取用户openid），snsapi_userinfo （弹出授权页面，可通过openid拿到昵称、性别、所在地。并且， 即使在未关注的情况下，只要用户授权，也能获取其信息 ）
        private String scope;
        //授权后重定向的回调链接地址， 请使用 urlEncode 对链接进行处理
        private String redirectUrl;
        //重定向后会带上state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节（非必传）
        private String state;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class AuthAccessToken extends ErrorMsg {
        //网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
        private String access_token;
        //access_token接口调用凭证超时时间，单位（秒）
        private Integer expires_in;
        //用户刷新access_token
        private String refresh_token;
        //用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
        private String openid;
        //用户授权的作用域，使用逗号（,）分隔
        private String scope;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class AuthUserInfo extends ErrorMsg {
        //用户的唯一标识
        private String openid;
        //用户昵称
        private String nickname;
        //用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
        private String sex;
        //用户个人资料填写的省份
        private String province;
        //普通用户个人资料填写的城市
        private String city;
        //国家，如中国为CN
        private String country;
        //用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
        private String headimgurl;
        //用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
        private String[] privilege;
        //只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
        private String unionid;
    }

    @Data
    @NoArgsConstructor
    public static class CloudEyeBaseRequest {
        //操作的接口名称，本接口取值：DetectAuth、GetDetectInfoEnhanced（必传）
        private String Action;
        //当前 UNIX 时间戳，可记录发起 API 请求的时间（必传）
        private Long Timestamp;
        //随机正整数，与 Timestamp 联合起来，用于防止重放攻击（必传）
        private Integer Nonce;
        //一个SecretId对应唯一的SecretKey，而SecretKey会用来生成请求签名Signature（必传）
        private String SecretId;
        //请求签名，用来验证此次请求的合法性（必传）
        private String Signature;
        //操作的 API 的版本，本接口取值：2018-03-01（必传）
        private String Version;
        //地域参数，本接口不需要传递此参数
        private String Region;
        //用于细分客户使用场景，自助接入里面创建：https://console.cloud.tencent.com/faceid
        private String RuleId;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class DetectAuthRequest extends CloudEyeBaseRequest {
        //姓名。（未使用OCR服务时，必须传入）最长长度32位。中文请使用UTF-8编码
        private String Name;
        //身份标识（未使用OCR服务时，必须传入），规则：a-zA-Z0-9组合。最长长度32位
        private String IdCard;
        //认证结束后重定向的回调链接地址。最长长度1024位
        private String RedirectUrl;
        //透传字段，在获取验证结果时返回
        private String Extra;
        //用于人脸比对的照片，Base64编码后的图片数据大小不超过3M，仅支持jpg、png格式。请使用标准的Base64编码方式(带=补位)，编码规范参考RFC4648
        private String ImageBase64;
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class GetDetectInfoRequest extends CloudEyeBaseRequest {
        //人脸核身流程的标识，调用DetectAuth接口时生成
        private String BizToken;
        ///指定拉取的结果信息（0:全部;1:文本类;2:身份证正反面;3:视频最佳截图照片;4:视频）如13表示拉取文本类、视频最佳截图信息。默认值0
        private String InfoType;
    }

    @Data
    @NoArgsConstructor
    public static class CloudEyeResponse {
        private Response response;

        @Data
        @NoArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Response {
            //一次核身流程的标识，有效时间为7,200秒，完成核身后，可用该标识获取验证结果信息
            private String BizToken;
            //用于发起核身流程的URL，仅微信H5场景使用
            private String Url;
            //唯一请求 ID，每次请求都会返回。定位问题时需要提供该次请求的 RequestId
            private String RequestId;
            //认证结果，json字符串
            private String DetectInfo;
            //异常信息
            private Error Error;

            @Data
            @NoArgsConstructor
            public static class Error {
                //异常标示
                private String Code;
                //异常信息说明
                private String Message;
            }
        }
    }

    @Data
    @NoArgsConstructor
    public static class TemplateMessageRequest {

        @JSONField(name = "touser")
        private String toUser;

        @JSONField(name = "template_id")
        private String templateId;

        @JSONField(name = "topcolor")
        private String topColor;

        private String url;

        private Map<String, TemplateMessageRequest.Data> data;

        @lombok.Data
        @AllArgsConstructor
        public static class Data {

            private String value;

            private String color;
        }
    }

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class FollowUser extends ErrorMsg {

        private Integer total;

        private Integer count;

        private Data data;

        @JSONField(name = "next_openid")
        private String nextOpenid;

        @lombok.Data
        @AllArgsConstructor
        public static class Data {

            @JSONField(name = "openid")
            private List<String> openIdList;
        }
    }
}