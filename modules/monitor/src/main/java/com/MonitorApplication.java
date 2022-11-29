package com;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author kuangq
 */
@EnableAdminServer
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class MonitorApplication {

    private static final Logger logger = LoggerFactory.getLogger(MonitorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
        logger.info("MonitorApplication start ...");
    }
}