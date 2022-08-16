package com.loyer.common.apis.config;

import com.loyer.common.dedicine.constant.SystemConst;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

/**
 * @author kuangq
 * @title FeignConfig
 * @description Feign请求配置
 * @date 2021-03-04 13:13
 */
@Configuration
public class FeignConfig implements RequestInterceptor {

    /**
     * @param requestTemplate
     * @return void
     * @author kuangq
     * @description 设置header，加入认证凭据
     * @date 2021-03-04 13:14
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(SystemConst.TOEKN_KEY, SystemConst.SECRET_KEY);
    }
}