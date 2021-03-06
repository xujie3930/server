package com.szmsd.putinstorage;

import com.szmsd.common.security.annotation.EnableCustomConfig;
import com.szmsd.common.security.annotation.EnableRyFeignClients;
import com.szmsd.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringCloudApplication
// @ComponentScan(basePackages = {"com.szmsd"})
public class BusinessPutinstorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessPutinstorageApplication.class, args);
    }

}
