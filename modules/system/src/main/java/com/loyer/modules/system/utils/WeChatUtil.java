package com.loyer.modules.system.utils;

import com.alibaba.fastjson.JSON;
import com.loyer.common.apis.server.ToolsServer;
import com.loyer.common.core.constant.PrefixConst;
import com.loyer.common.core.constant.SuffixConst;
import com.loyer.common.core.utils.common.ParamsUtil;
import com.loyer.common.core.utils.document.FileUtil;
import com.loyer.common.core.utils.encrypt.AesUtil;
import com.loyer.common.core.utils.reflect.ContextUtil;
import com.loyer.common.core.utils.request.HttpUtil;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.dedicine.utils.GeneralUtil;
import com.loyer.common.dedicine.utils.StringUtil;
import com.loyer.common.redis.utils.RedisUtil;
import com.loyer.modules.system.entity.TencentEntity;
import com.loyer.modules.system.entity.WeChatConfig;
import com.loyer.modules.system.inherit.WechatHttpMessageConverter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author kuangq
 * @title WeChatUtil
 * @description 微信SDK工具类
 * @date 2020-05-30 18:43
 */
public class WeChatUtil {

    private static final Logger logger = LoggerFactory.getLogger(WeChatUtil.class);

    private static final WeChatConfig WE_CHAT_CONFIG = ContextUtil.getBean(WeChatConfig.class);

    private static final RedisUtil REDIS_UTIL = ContextUtil.getBean(RedisUtil.class);

    private static final String VOICE_PATH = SystemConst.LOCAL_FILE_PATH + "voice/";

    //校验媒体文件的最小阀值
    private static final int MIN_LENGTH = 128;

    /**
     * @param signature
     * @param timestamp
     * @param nonce
     * @return boolean
     * @author kuangq
     * @description 微信公众号服务器配置校验
     * @date 2020-06-20 22:30
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) {
        //将token、timestamp、nonce三个参数进行字典序排序
        String[] ary = new String[]{SystemConst.LOYER, timestamp, nonce};
        Arrays.sort(ary);
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : ary) {
            stringBuilder.append(str);
        }
        //获取sha1加密字符串
        String temp = encode(stringBuilder.toString());
        //将sha1加密后的字符串可与微信请求传来的signature对比
        return temp.equals(signature.toLowerCase());
    }

    /**
     * @param jsSdk
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author kuangq
     * @description 获取微信JSSDK接口配置
     * @date 2019-08-28 23:30
     */
    public static Map<String, Object> getConfig(TencentEntity.JsSdk jsSdk) {
        //校验入参如果未传appID和appSecret则取默认配置
        checkParams(jsSdk);
        //组装前端wx.config接口的入参
        Map<String, Object> config = new HashMap<>(8);
        config.put("noncestr", GeneralUtil.getUuid());
        config.put("jsapi_ticket", getApiTicket(jsSdk));
        config.put("timestamp", System.currentTimeMillis() / 1000);
        config.put("url", jsSdk.getUrl());
        //根据前四个参数加密生成验签
        config.put("signature", encode(ParamsUtil.sealingParams(config)));
        config.put("appId", jsSdk.getAppId());
        //去除无需返回前端的参数
        config.remove("url");
        config.remove("jsapi_ticket");
        return config;
    }

    /**
     * @param jsSdk
     * @return void
     * @author kuangq
     * @description 入参校验
     * @date 2020-11-09 17:55
     */
    private static void checkParams(TencentEntity.JsSdk jsSdk) {
        Objects.requireNonNull(jsSdk, HintEnum.HINT_1014.getMsg());
        //必传前端调用jssdk接口的页面地址
        if (StringUtils.isBlank(jsSdk.getUrl())) {
            throw new BusinessException(HintEnum.HINT_1015);
        }
        //前端未传appID则取默认配置
        if (StringUtils.isBlank(jsSdk.getAppId())) {
            jsSdk.setAppId(WE_CHAT_CONFIG.getAppId());
        }
        //前端未传appSecret则取默认配置
        if (StringUtils.isBlank(jsSdk.getAppSecret())) {
            jsSdk.setAppSecret(WE_CHAT_CONFIG.getAppSecret());
        }
    }

    /**
     * @param message
     * @return java.lang.String
     * @author kuangq
     * @description SHA1算法加密
     * @date 2019-08-28 23:20
     */
    @SneakyThrows
    private static String encode(String message) {
        //创建getInstance静态函数来进行实例化和初始化
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        //将三个参数字符串拼接成一个字符串进行sha1加密
        byte[] digest = messageDigest.digest(StringUtil.decode(message));
        //返回加密字符串
        return StringUtil.hexEncode(digest);
    }

    /**
     * @param jsSdk
     * @return java.lang.String
     * @author kuangq
     * @description 获取accessToken令牌
     * @date 2019-08-28 23:30
     */
    private static String getAccessToken(TencentEntity.JsSdk jsSdk) {
        //拼接缓存key值
        String key = PrefixConst.WE_CHAT_ACCESS_TOKEN + jsSdk.getAppId();
        //如果存在直接获取否则调接口获取
        if (REDIS_UTIL.isExist(key)) {
            return REDIS_UTIL.getValue(key).toString();
        }
        //接口地址从默认配置中获取
        String getTokenUrl = String.format(WE_CHAT_CONFIG.getGetTokenUrl(), jsSdk.getAppId(), jsSdk.getAppSecret());
        //发起get请求
        TencentEntity.AccessToken accessToken = HttpUtil.doGet(getTokenUrl, TencentEntity.AccessToken.class);
        logger.info("【accessToken】{}", JSON.toJSONString(accessToken));
        //出参校验
        Objects.requireNonNull(accessToken, HintEnum.HINT_1012.getMsg());
        if (accessToken.getErrcode() != null && accessToken.getErrcode() != 0) {
            throw new BusinessException(HintEnum.HINT_1012, accessToken);
        }
        //缓存accessToken过期时长设置与微信服务器一致默认7200s
        REDIS_UTIL.setValue(key, accessToken.getAccess_token(), (long) accessToken.getExpires_in());
        return accessToken.getAccess_token();
    }

    /**
     * @param jsSdk
     * @return java.lang.String
     * @author kuangq
     * @description 获取apiTicket
     * @date 2019-08-28 23:30
     */
    private static String getApiTicket(TencentEntity.JsSdk jsSdk) {
        //拼接缓存key值
        String key = PrefixConst.WE_CHAT_API_TICKET + jsSdk.getAppId();
        //如果存在直接获取否则调接口获取
        if (REDIS_UTIL.isExist(key)) {
            return REDIS_UTIL.getValue(key).toString();
        }
        //接口地址从默认配置中获取
        String getTicketUrl = String.format(WE_CHAT_CONFIG.getGetTicketUrl(), getAccessToken(jsSdk));
        //发起get请求
        TencentEntity.ApiTicket apiTicket = HttpUtil.doGet(getTicketUrl, TencentEntity.ApiTicket.class);
        logger.info("【apiTicket】{}", JSON.toJSONString(apiTicket));
        //出参校验
        if (apiTicket == null || apiTicket.getErrcode() != 0) {
            throw new BusinessException(HintEnum.HINT_1013, apiTicket);
        }
        //缓存apiTicket过期时长设置与微信服务器一致默认7200s
        REDIS_UTIL.setValue(key, apiTicket.getTicket(), (long) apiTicket.getExpires_in());
        return apiTicket.getTicket();
    }

    /**
     * @param mediaId
     * @param toolsServer
     * @return com.loyer.system.common.entity.ApiResult
     * @author kuangq
     * @description 下载微信服务器上的媒体文件
     * @date 2020-07-08 19:05
     */
    public static ApiResult downloadMedia(String mediaId, ToolsServer toolsServer) {
        TencentEntity.JsSdk jsSdk = new TencentEntity.JsSdk();
        //设置默认appid和appSecret
        jsSdk.setAppId(WE_CHAT_CONFIG.getAppId());
        jsSdk.setAppSecret(WE_CHAT_CONFIG.getAppSecret());
        //获取接口入参accessToken
        String accessToken = getAccessToken(jsSdk);
        //拼接获取媒体文件的url
        String getMediaUrl = String.format(WE_CHAT_CONFIG.getGetMediaUrl(), accessToken, mediaId);
        //获取接口返回的二进制文件
        byte[] bytes = HttpUtil.doGet(getMediaUrl, byte[].class);
        //判断文件大小默认小于128个字节为空
        if (bytes.length < MIN_LENGTH) {
            return ApiResult.hintEnum(HintEnum.HINT_1069, bytes.length);
        }
        //生成文件路径
        String filePath = VOICE_PATH + GeneralUtil.getUuid();
        String amrPath = filePath + SuffixConst.ARM;
        String mp3Path = filePath + SuffixConst.MP3;
        //保存文件
        FileUtil.saveFile(bytes, amrPath);
        //调用远程服务将amr转为mp3格式
        ApiResult apiResult = toolsServer.armToMp3(amrPath, mp3Path);
        //判断请求是否成功
        if (!apiResult.getFlag()) {
            return apiResult;
        }
        //获取文件所在服务器的url
        String url = mp3Path.substring(mp3Path.indexOf("voice/"));
        return ApiResult.success(url);
    }

    /**
     * @param authorize
     * @return com.loyer.system.common.entity.ApiResult
     * @author kuangq
     * @description 获取微信网页授权登录的跳转链接
     * @date 2020-07-10 16:13
     */
    public static ApiResult getAuthUserInfoUrl(TencentEntity.Authorize authorize) {
        //校验入参
        if (StringUtils.isBlank(authorize.getScope()) || StringUtils.isBlank(authorize.getRedirectUrl())) {
            throw new BusinessException(HintEnum.HINT_1071);
        }
        //接口地址从默认配置中获取
        String getTicketUrl = String.format(WE_CHAT_CONFIG.getGetAuthCodeUrl(), WE_CHAT_CONFIG.getAppId(), authorize.getScope(), authorize.getRedirectUrl(), authorize.getState());
        //该链接只能在微信端打开
        return ApiResult.success(getTicketUrl);
    }

    /**
     * @param code
     * @param state
     * @return java.lang.String
     * @author kuangq
     * @description 网页授权获取用户信息
     * @date 2020-07-10 18:03
     */
    @SneakyThrows
    public static String getAuthUserInfo(String code, String state) {
        //处理以下两个微信接口返回参数类型为text/plain的问题，添加HttpMessageConverter转换支持
        RestTemplate restTemplate = HttpUtil.restTemplate;
        restTemplate.getMessageConverters().add(new WechatHttpMessageConverter());
        //获取AccessToken
        String getAccessTokenUrl = String.format(WE_CHAT_CONFIG.getGetAccessTokenUrl(), WE_CHAT_CONFIG.getAppId(), WE_CHAT_CONFIG.getAppSecret(), code);
        TencentEntity.AuthAccessToken authAccessToken = restTemplate.getForObject(getAccessTokenUrl, TencentEntity.AuthAccessToken.class);
        //失败校验
        if (authAccessToken == null || authAccessToken.getErrcode() != null) {
            throw new BusinessException(HintEnum.HINT_1012, authAccessToken);
        }
        //获取用户基本信息
        String getUserInfoUrl = String.format(WE_CHAT_CONFIG.getGetUserInfoUrl(), authAccessToken.getAccess_token(), authAccessToken.getOpenid());
        //处理微信接口返回信息编码不一致问题
        String authUserInfoStr = new String(HttpUtil.doGet(getUserInfoUrl, String.class).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        logger.info("【authUserInfoStr】：{}", authUserInfoStr);
        TencentEntity.AuthUserInfo authUserInfo = JSON.parseObject(authUserInfoStr, TencentEntity.AuthUserInfo.class);
        //失败校验
        if (authUserInfo.getErrcode() != null) {
            throw new BusinessException(HintEnum.HINT_1012, authUserInfo);
        }
        //缓存微信授权用户信息
        String key = PrefixConst.WE_CHAT_AUTH_USER_INFO + authUserInfo.getOpenid();
        REDIS_UTIL.setValue(key, authUserInfo, 3600 * 6L);
        //加密用户昵称拼接到路由上
        return state + AesUtil.encrypt(authUserInfo.getNickname(), SystemConst.SECRET_KEY);
    }
}