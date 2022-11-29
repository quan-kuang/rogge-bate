package com.loyer.common.apis.config;

import com.loyer.common.dedicine.constant.SystemConst;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

/**
 * Feign请求配置
 *
 * @author kuangq
 * @date 2021-03-04 13:13
 */
@Configuration
public class FeignConfig implements RequestInterceptor {

    /**
     * 设置header，加入认证凭据
     *
     * @author kuangq
     * @date 2021-03-04 13:14
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(SystemConst.TOEKN_KEY, SystemConst.SECRET_KEY);
    }
}