package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author kuangq
 */
@EnableAsync
@EnableScheduling
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class SystemApplication {

    private static final Logger logger = LoggerFactory.getLogger(SystemApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
        logger.info("SystemApplication start ...");
    }
}