package com.szmsd.common.core.command;

import com.szmsd.common.core.support.Context;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BasicCommandConfiguration {

    @Bean
    public Context context(ApplicationContext applicationContext) {
        Context.applicationContext = applicationContext;
        return new Context();
    }


}
