package com.szmsd.returnex;

import com.szmsd.common.security.annotation.EnableCustomConfig;
import com.szmsd.common.security.annotation.EnableRyFeignClients;
import com.szmsd.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @ClassName: ReturnExpressApplication
 * @Description: 退件服务
 * @Author: 11
 * @Date: 2021/3/26 11:31
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringCloudApplication
@EnableScheduling
@EnableAsync
public class BusinessReturnExpressApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessReturnExpressApplication.class, args);
    }

}
