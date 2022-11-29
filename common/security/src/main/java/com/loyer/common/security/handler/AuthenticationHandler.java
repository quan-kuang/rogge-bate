package com.loyer.common.security.handler;

import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.security.entity.LoginUser;
import com.loyer.common.security.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Security的认证处理器（基于token做过滤）
 *
 * @author kuangq
 * @date 2020-08-07 16:04
 */
@Component
public class AuthenticationHandler extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            //根据header中的token获取缓存的用户信息
            String token = httpServletRequest.getHeader(SystemConst.TOEKN_KEY);
            LoginUser loginUser = SecurityUtil.getLoginUser(token);
            //loginUser不为空进入校验
            if (loginUser != null && SecurityUtil.getAuthentication() == null) {
                //放行接口测试
                if (!SystemConst.LOYER.equals(loginUser.getToken())) {
                    logger.info("【用户缓存刷新】{}：{}", loginUser.getUsername(), SecurityUtil.refresh(loginUser.getToken()));
                }
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (BusinessException businessException) {
            //捕获自定义异常直接返回
            ApiResult apiResult = ApiResult.failure(businessException);
            AuthenticationFailureHandler.renderJson(httpServletResponse, apiResult);
        }
    }
}