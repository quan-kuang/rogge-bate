package com.loyer.common.core.config;

import com.google.common.base.Predicates;
import com.loyer.common.core.utils.common.DateUtil;
import com.loyer.common.dedicine.constant.SystemConst;
import com.loyer.common.dedicine.utils.DesUtil;
import com.loyer.common.dedicine.utils.GeneralUtil;
import com.loyer.common.dedicine.utils.Md5Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Swagger2可视化接口测试（访问路径：/swagger-ui.html、/doc.html）
 *
 * @author kuangq
 * @date 2019-10-14 14:36
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Value("${swaggerUrl}")
    private String swaggerUrl;

    //网关应用名称
    @Value("${spring.application.name}")
    private String name;

    /**
     * docket配置
     *
     * @author kuangq
     * @date 2019-10-14 16:59
     */
    @SuppressWarnings("Guava")
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                //定义API文档汇总信息
                .apiInfo(getApiInfo())
                //设置host处理生产环境nginx代理问题
                .host(swaggerUrl)
                //设置信息头
                .globalOperationParameters(getHeaders())
                //对所有包含注解的接口进行监控
                .select().apis(RequestHandlerSelectors.any())
                //错误路径不监控
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                //创建
                .build()
                //设置安全认证token
                .securitySchemes(getApiKeyList())
                //设置安全上下文
                .securityContexts(getSecurityContextList());
    }

    /**
     * 创建信息头
     *
     * @author kuangq
     * @date 2019-10-14 16:57
     */
    private List<Parameter> getHeaders() {
        ParameterBuilder parameterBuilder = new ParameterBuilder();
        parameterBuilder.modelRef(new ModelRef("string")).parameterType("header").required(true);
        String noncestr = GeneralUtil.getUuid();
        String timestamp = DateUtil.getTimestamp();
        String signature = Md5Util.encrypt(DesUtil.encrypt(noncestr + "&" + timestamp));
        return new ArrayList<Parameter>() {{
            add(parameterBuilder.name("noncestr").description("随机字符串").defaultValue(noncestr).build());
            add(parameterBuilder.name("timestamp").description("时间戳").defaultValue(timestamp).build());
            add(parameterBuilder.name("signature").description("签名").defaultValue(signature).build());
        }};
    }

    /**
     * 安全模式，这里指定token通过Authorization头请求头传递
     *
     * @author kuangq
     * @date 2021-12-21 14:17
     */
    private List<ApiKey> getApiKeyList() {
        return newArrayList(new ApiKey("Authorization", "token", "header"));
    }

    /**
     * 安全上下文
     *
     * @author kuangq
     * @date 2021-12-21 14:18
     */
    private List<SecurityContext> getSecurityContextList() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopeAry = new AuthorizationScope[1];
        authorizationScopeAry[0] = authorizationScope;
        List<SecurityReference> securityReferenceList = newArrayList(new SecurityReference("Authorization", authorizationScopeAry));
        return newArrayList(SecurityContext.builder().securityReferences(securityReferenceList).forPaths(PathSelectors.any()).build());
    }

    /**
     * 创建Api接口信息
     *
     * @author kuangq
     * @date 2019-10-14 16:57
     */
    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                //文档页标题
                .title(name)
                //详细信息
                .description("API文档")
                //版本号
                .version(SystemConst.VERSION)
                //联系人信息
                .contact(new Contact("kuangq", "https://loyer.wang", "931851631@qq.com"))
                .build();
    }
}