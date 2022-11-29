package com.loyer.common.core.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * 错误页面配置
 *
 * @author kuangq
 * @date 2019-10-15 14:21
 */
@Configuration
public class ErrorPageConfig implements ErrorPageRegistrar {

    /**
     * 配置异常跳转
     *
     * @author kuangq
     * @date 2019-10-15 15:30
     */
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        /*错误类型为404，找不到网页*/
        ErrorPage e404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error/e404");
        /*错误类型为500，服务器响应错误*/
        ErrorPage e500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/e500");
        registry.addErrorPages(e404, e500);
    }
}