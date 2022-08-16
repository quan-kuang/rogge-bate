package com.loyer.common.core.utils.request;

import com.loyer.common.core.constant.SpecialCharsConst;
import lombok.SneakyThrows;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author kuangq
 * @title CookieUtil
 * @description Cookie工具类
 * @date 2020-11-21 11:08
 */
public class CookieUtil {

    /**
     * @param
     * @return javax.servlet.http.HttpServletRequest
     * @author kuangq
     * @description 获取HttpServletRequest
     * @date 2020-11-21 11:25
     */
    public static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) Objects.requireNonNull(requestAttributes)).getRequest();
    }

    /**
     * @param
     * @return javax.servlet.http.HttpServletResponse
     * @author kuangq
     * @description 获取HttpServletResponse
     * @date 2020-11-21 11:27
     */
    public static HttpServletResponse getHttpServletResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) Objects.requireNonNull(requestAttributes)).getResponse();
    }

    /**
     * @param name
     * @return java.lang.String
     * @author kuangq
     * @description 获取指定的cookie值
     * @date 2020-11-21 12:06
     */
    @SneakyThrows
    public static String getCookieValue(String name) {
        Cookie[] cookies = getHttpServletRequest().getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        Cookie result = Arrays.stream(cookies).filter(cookie -> name.equals(cookie.getName())).findFirst().orElse(null);
        if (result == null) {
            return null;
        }
        return URLDecoder.decode(result.getValue(), StandardCharsets.UTF_8.name());
    }

    /**
     * @param name
     * @param value
     * @param maxAge
     * @return void
     * @author kuangq
     * @description 设置Cookie
     * @date 2020-11-21 12:50
     */
    @SneakyThrows
    public static void setCookie(String name, String value, int maxAge) {
        value = URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        setDomain(cookie);
        getHttpServletResponse().addCookie(cookie);
    }

    /**
     * @param cookie
     * @return void
     * @author kuangq
     * @description 设置域名
     * @date 2020-11-21 12:50
     */
    private static void setDomain(Cookie cookie) {
        //获取请求地址
        String domain = getHttpServletRequest().getRequestURL().toString();
        //截取http://或https://
        if (domain.indexOf(SpecialCharsConst.DOUBLE_SLASH) > 0) {
            domain = domain.split(SpecialCharsConst.DOUBLE_SLASH)[1];
        }
        //截取端口号:
        if (domain.indexOf(SpecialCharsConst.COLON) > 0) {
            domain = domain.split(SpecialCharsConst.COLON)[0];
        }
        //截取接口地址
        if (domain.indexOf(SpecialCharsConst.SLASH) > 0) {
            domain = domain.split(SpecialCharsConst.SLASH)[0];
        }
        cookie.setDomain(domain);
    }
}