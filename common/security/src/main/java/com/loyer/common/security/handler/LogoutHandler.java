package com.loyer.common.security.handler;

import com.loyer.common.core.constant.PrefixConst;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.redis.utils.CacheUtil;
import com.loyer.common.security.utils.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Security登出处理器
 *
 * @author kuangq
 * @date 2020-08-14 15:29
 */
@Component
public class LogoutHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        try {
            ApiResult apiResult = ApiResult.failure("登出异常");
            //获取header中的token
            String token = httpServletRequest.getHeader(SystemConst.TOEKN_KEY);
            if (StringUtils.isNotBlank(token)) {
                //解密token
                String decodeToken = SecurityUtil.getDecodeToken(token);
                //删除用户在线信息
                CacheUtil.KEY.delete(PrefixConst.ONLINE_USER + decodeToken);
                //删除缓存数据
                CacheUtil.KEY.delete(PrefixConst.LOGIN_USER + decodeToken);
                //返回
                apiResult = ApiResult.success("登出成功");
            }
            AuthenticationFailureHandler.renderJson(httpServletResponse, apiResult);
        } catch (BusinessException businessException) {
            //捕获自定义异常直接返回
            ApiResult apiResult = ApiResult.failure(businessException);
            AuthenticationFailureHandler.renderJson(httpServletResponse, apiResult);
        }
    }
}