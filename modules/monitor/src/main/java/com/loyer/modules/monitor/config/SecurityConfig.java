package com.loyer.modules.monitor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Security配置
 *
 * @author kuangq
 * @date 2022-08-24 16:04
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                //开启请求过滤
                .authorizeRequests()
                //登录页面放行
                .antMatchers(contextPath + "/login").permitAll()
                //anyRequest匹配所有路径请求，都需要鉴权
                .anyRequest().authenticated()
                //禁用CSRF功能
                .and().csrf().disable().httpBasic()
                //禁用frameOptions
                .and().headers().frameOptions().disable()
                //启用spring security默认登录页面
                .and().formLogin()
                //禁用session
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
        ;
    }
}