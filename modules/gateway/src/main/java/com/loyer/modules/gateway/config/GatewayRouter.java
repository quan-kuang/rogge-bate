package com.loyer.modules.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.HashMap;

/**
 * @author kuangq
 * @title GatewayRouter
 * @description 网关配置
 * @date 2019-10-18 16:12
 */
@Configuration
public class GatewayRouter {

    /**
     * @param
     * @return org.springframework.web.reactive.function.server.RouterFunction<org.springframework.web.reactive.function.server.ServerResponse>
     * @author kuangq
     * @description index，避免直接访问提示404问题
     * @date 2021-12-20 16:41
     */
    @Bean
    public RouterFunction<ServerResponse> index() {
        return RouterFunctions.route(
                RequestPredicates.path("/"), request -> ServerResponse.ok().build()
        );
    }

    /**
     * @param
     * @return org.springframework.web.reactive.function.server.RouterFunction<org.springframework.web.reactive.function.server.ServerResponse>
     * @author kuangq
     * @description 处理swagger会访问csrf接口前端报错
     * @date 2021-12-20 16:41
     */
    @Bean
    public RouterFunction<ServerResponse> csrf() {
        return RouterFunctions.route(
                RequestPredicates.path("/csrf"), request -> ServerResponse.ok().body(BodyInserters.fromValue(new HashMap<>(1)))
        );
    }

    /**
     * @param
     * @return org.springframework.web.reactive.function.server.RouterFunction<org.springframework.web.reactive.function.server.ServerResponse>
     * @author kuangq
     * @description 路由器
     * @date 2021-03-04 15:03
     */
    @Bean
    public RouterFunction<ServerResponse> testFunRouterFunction() {
        return RouterFunctions.route(
                RequestPredicates.path("/showVersion"), request -> ServerResponse.ok().body(BodyInserters.fromValue("罗格贝特"))
        );
    }

    /**
     * @param routeLocatorBuilder
     * @return org.springframework.cloud.gateway.route.RouteLocator
     * @author kuangq
     * @description 过滤器设置，监听路径服务转发
     * @date 2021-03-04 14:59
     */
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("system", r -> r.path("/system/**").filters(f -> f.stripPrefix(1)).uri("lb://system"))
                .route("message", r -> r.path("/message/**").filters(f -> f.stripPrefix(1)).uri("lb://message"))
                .route("tools", r -> r.path("/tools/**").filters(f -> f.stripPrefix(1)).uri("lb://tools"))
                .build();
    }
}