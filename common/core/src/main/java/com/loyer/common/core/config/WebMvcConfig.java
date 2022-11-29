package com.loyer.common.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * WebMvc设置
 *
 * @author kuangq
 * @date 2021-03-04 23:00
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 添加静态资源的直接访问
     *
     * @author kuangq
     * @date 2019-10-15 16:43
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*将所有资源都映射到classpath:/static/目录下*/
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        /*引入模板引擎，映射/static/**下资源*/
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        /*swagger2静态资源文件*/
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        super.addResourceHandlers(registry);
    }

    /**
     * 配置内部资源视图解析器
     *
     * @author kuangq
     * @date 2021-03-05 10:49
     */
    @Override
    protected void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        //配置前缀，不用加static文件夹路径，后面有文件夹路径在加
        viewResolver.setPrefix("/");
        //设置默认后缀
        viewResolver.setSuffix(".html");
        registry.viewResolver(viewResolver);
        super.configureViewResolvers(registry);
    }
}