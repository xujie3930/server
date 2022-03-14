package com.szmsd.gateway;

import com.alibaba.fastjson.JSON;
import com.szmsd.gateway.config.IgnoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 网关启动程序
 * 
 * @author szmsd
 */
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class SzmsdGatewayApplication
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SzmsdGatewayApplication.class);

    public static void main(String[] args)
    {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(SzmsdGatewayApplication.class, args);
        LOGGER.info("(♥◠‿◠)ﾉﾞ  敏思达网关启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");

        IgnoreConfig ignoreConfig = applicationContext.getBean(IgnoreConfig.class);
        LOGGER.info("ignoreConfig: {}", JSON.toJSONString(ignoreConfig));
    }
}