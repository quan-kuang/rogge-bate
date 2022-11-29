package com.loyer.modules.gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger.web.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 提供swagger需要访问的接口
 *
 * @author kuangq
 * @date 2021-12-20 15:56
 */
@RestController
@RequestMapping("swagger-resources")
public class SwaggerRouter {

    @Resource
    private SwaggerProvider swaggerProvider;

    @RequestMapping
    public ResponseEntity<List<SwaggerResource>> swaggerResources() {
        return new ResponseEntity<>(swaggerProvider.get(), HttpStatus.OK);
    }

    @RequestMapping(value = "configuration/ui")
    public ResponseEntity<UiConfiguration> uiConfiguration() {
        return new ResponseEntity<>(UiConfigurationBuilder.builder().build(), HttpStatus.OK);
    }

    @RequestMapping(value = "configuration/security")
    public ResponseEntity<SecurityConfiguration> securityConfiguration() {
        return new ResponseEntity<>(SecurityConfigurationBuilder.builder().build(), HttpStatus.OK);
    }
}