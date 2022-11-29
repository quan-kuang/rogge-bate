package com.loyer.common.security.config;

import com.loyer.common.security.handler.AuthenticationFailureHandler;
import com.loyer.common.security.handler.AuthenticationHandler;
import com.loyer.common.security.handler.LogoutHandler;
import com.loyer.common.security.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

/**
 * Security配置
 *
 * @author kuangq
 * @date 2020-08-07 16:04
 */
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //自定义用户认证
    @Resource
    private UserDetailsServiceImpl userDetailsService;

    //登出处理
    @Resource
    private LogoutHandler logoutHandler;

    //基于token的认证处理
    @Resource
    private AuthenticationHandler authenticationHandler;

    //认证失败的处理
    @Resource
    private AuthenticationFailureHandler authenticationFailureHandler;

    /**
     * 处理AuthenticationManager直接注入异常
     *
     * @author kuangq
     * @date 2020-08-14 14:50
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 登录身份认证
     *
     * @author kuangq
     * @date 2020-08-14 14:53
     */
    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                //开启请求过滤
                .authorizeRequests()
                //放行跨域预检请求
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                //放行静态资源访问
                .antMatchers("/favicon.ico", "/static/**").permitAll()
                //放行swagger相关资源
                .antMatchers("/webjars/**", "/swagger-resources/**", "/swagger-ui.html", "/v2/api-docs", "/doc.html", "/csrf").permitAll()
                //放行测试接口
                .antMatchers("/", "/demo/**").permitAll()
                //特殊接口允许匿名访问，不允许已登入用户访问sendCaptcha
                .antMatchers("/user/login", "/user/messageLogin", "/util/getCaptcha", "/news/sendCaptcha", "/wechat/link", "/wechat/getAuthUserInfo", "/websocket/*/*/*/*").anonymous()
                //anyRequest匹配所有路径请求，除上面的都需要鉴权
                .anyRequest().authenticated()
                //启用http基础验证
                .and().httpBasic()
                //禁用CSRF功能
                .and().csrf().disable().httpBasic()
                //禁用frameOptions
                .and().headers().frameOptions().disable()
                //启用spring security默认登录页面
                .and().formLogin()
                //添加登出的处理
                .and().logout().logoutUrl("/logout").logoutSuccessHandler(logoutHandler)
                //安全认证失败处理
                .and().exceptionHandling().authenticationEntryPoint(authenticationFailureHandler)
                //添加JWT进行token认证过滤
                .and().addFilterBefore(authenticationHandler, UsernamePasswordAuthenticationFilter.class)
                //基于token，所以不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        ;
    }
}