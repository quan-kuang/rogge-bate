package com.loyer.common.security.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.enums.HintEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * Security认证失败的处理器
 *
 * @author kuangq
 * @date 2020-08-07 14:59
 */
@Component
public class AuthenticationFailureHandler implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFailureHandler.class);

    /**
     * 渲染返回json
     *
     * @author kuangq
     * @date 2020-08-14 14:13
     */
    public static void renderJson(HttpServletResponse httpServletResponse, Object object) {
        try {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
            //处理json序列化会过滤掉null值
            httpServletResponse.getWriter().print(JSON.toJSONString(object, SerializerFeature.WriteMapNullValue));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) {
        ApiResult apiResult = ApiResult.hintEnum(HintEnum.HINT_1088, httpServletRequest.getRequestURI());
        renderJson(httpServletResponse, apiResult);
    }
}