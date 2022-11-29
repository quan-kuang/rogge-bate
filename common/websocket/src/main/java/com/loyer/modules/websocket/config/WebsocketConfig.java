package com.loyer.modules.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebsocketConfig配置类，注入对象ServerEndpointExporter，该bean将自动注册使用@ServerEndpoint注解的Websocket endpoint
 *
 * @author kuangq
 * @date 2021-07-18 1:34
 */
@Configuration
public class WebsocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}