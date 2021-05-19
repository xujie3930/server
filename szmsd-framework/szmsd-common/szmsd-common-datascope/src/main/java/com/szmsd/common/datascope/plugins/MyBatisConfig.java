package com.szmsd.common.datascope.plugins;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangyuyuan
 * @date 2020-12-02 002 17:52
 */
@Configuration
public class MyBatisConfig {

    @Bean
    public SqlDataScopeInterceptor sqlDataScopeInterceptor() {
        return new SqlDataScopeInterceptor();
    }
}
