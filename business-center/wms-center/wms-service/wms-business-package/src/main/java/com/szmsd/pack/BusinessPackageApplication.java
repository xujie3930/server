package com.szmsd.pack;

import com.szmsd.common.security.annotation.EnableCustomConfig;
import com.szmsd.common.security.annotation.EnableRyFeignClients;
import com.szmsd.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @ClassName: BusinessPackageApplication
 * @Description: 收货管理
 * @Author: 11
 * @Date: 2021/4/1 10:02
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringCloudApplication
@EnableScheduling
public class BusinessPackageApplication {
    public static void main(String[] args) {
        SpringApplication.run(BusinessPackageApplication.class, args);
    }
}
