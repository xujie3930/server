package com.szmsd.open;

import com.szmsd.common.redis.configure.RedisConfig;
import com.szmsd.common.security.annotation.EnableCustomConfig;
import com.szmsd.common.security.annotation.EnableRyFeignClients;
import com.szmsd.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@EnableScheduling
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, RedisConfig.class})
@EnableDiscoveryClient
@EnableCircuitBreaker
public class BusinessOpenApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessOpenApplication.class, args);
    }

}
