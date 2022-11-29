package com.loyer.common.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域处理器
 *
 * @author kuangq
 * @date 2019-08-26 10:20
 */
@Configuration
public class CorsFilterConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //设置缓存跨域访问信息时长
        corsConfiguration.setMaxAge(3600L);
        //设置启用cookie
        corsConfiguration.setAllowCredentials(true);
        //设置允许跨域请求的域名
        corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL);
        //设置允许跨域的请求头
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        //设置允许跨域的请求类型
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        //设置URL的映射路径
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
}