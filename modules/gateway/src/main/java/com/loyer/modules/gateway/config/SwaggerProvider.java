package com.loyer.modules.gateway.config;

import com.loyer.common.dedicine.constant.SystemConst;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 聚合多个服务的swagger
 *
 * @author kuangq
 * @date 2021-12-20 15:09
 */
@Component
public class SwaggerProvider implements SwaggerResourcesProvider {

    //添加统一前缀，swagger2默认的url后缀
    private static final String SWAGGER2_URL = "/%s/v2/api-docs";

    //网关应用名称
    @Value("${spring.application.name}")
    private String name;

    //网关路由
    @Resource
    private RouteLocator routeLocator;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> swaggerResourceList = new ArrayList<>();
        List<String> routeList = new ArrayList<>();
        // 获取所有可用的host：serviceId
        routeLocator.getRoutes().filter((route) -> !name.equals(route.getId())).subscribe(route -> routeList.add(route.getId()));
        // 记录已经添加过的server，避免eureka上注册多个同名服务
        Set<String> exist = new HashSet<>();
        routeList.forEach(route -> {
            if (!exist.contains(route)) {
                exist.add(route);
                swaggerResourceList.add(getSwaggerResource(route));
            }
        });
        return swaggerResourceList;
    }

    /**
     * 设置SwaggerResource
     *
     * @author kuangq
     * @date 2021-12-20 16:21
     */
    private SwaggerResource getSwaggerResource(String route) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(route);
        swaggerResource.setSwaggerVersion(SystemConst.VERSION);
        swaggerResource.setLocation(String.format(SWAGGER2_URL, route));
        return swaggerResource;
    }
}