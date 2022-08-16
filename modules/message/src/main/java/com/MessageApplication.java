package com;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author kuangq
 */
@EnableAsync
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class MessageApplication {

    private static final Logger logger = LoggerFactory.getLogger(MessageApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MessageApplication.class, args);
        logger.info("MessageApplication start ...");
    }
}