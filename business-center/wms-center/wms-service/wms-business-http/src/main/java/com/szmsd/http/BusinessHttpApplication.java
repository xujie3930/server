package com.szmsd.http;

import com.szmsd.common.security.annotation.EnableCustomConfig;
import com.szmsd.common.security.annotation.EnableRyFeignClients;
import com.szmsd.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@EnableScheduling
@SpringCloudApplication
public class BusinessHttpApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessHttpApplication.class, args);
    }

}
