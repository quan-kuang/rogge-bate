package com.loyer.common.core.utils.common;

import com.alibaba.fastjson.JSONObject;
import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.core.utils.request.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * IP地址工具类
 *
 * @author kuangq
 * @date 2020-12-15 16:40
 */
public class IpUtil {

    private static final Logger logger = LoggerFactory.getLogger(IpUtil.class);

    //获取地域位置信息的接口地址
    private static final String GET_POSITION_URL = "https://whois.pconline.com.cn/ipJson.jsp?json=true&ip=%s";

    //本地IP
    private static final List<String> LOCAL = Arrays.asList("127.0.0.1", "localhost");

    /**
     * 获取客户端真实IP地址
     *
     * @author kuangq
     * @date 2020-07-29 17:17
     */
    public static String getRealIp(HttpServletRequest httpServletRequest) {
        String ip = null;
        String[] keys = {"X-Real-IP", "X-Forwarded-For"};
        for (String key : keys) {
            ip = httpServletRequest.getHeader(key);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                break;
            }
        }
        if (ip == null) {
            ip = httpServletRequest.getRemoteAddr();
        }
        if (ip != null && ip.contains(SpecialCharsConst.COMMA)) {
            ip = StringUtils.substringAfterLast(ip, SpecialCharsConst.COMMA);
        }
        return ip;
    }

    /**
     * 根据ip获取地理位置
     *
     * @author kuangq
     * @date 2020-12-15 16:56
     */
    public static String getPosition(String ip) {
        try {
            if (StringUtils.isBlank(ip) || LOCAL.contains(ip)) {
                return "localhost";
            }
            String getPositionUrl = String.format(GET_POSITION_URL, ip);
            String result = HttpUtil.doGet(getPositionUrl, String.class);
            JSONObject jsonObject = JSONObject.parseObject(result, JSONObject.class);
            return jsonObject.getString("addr");
        } catch (Exception e) {
            logger.error("【获取位置信息异常】{}", e.getMessage());
            return "unknown";
        }
    }
}