package com.szmsd.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @FileName WebSocketConfig.java
 * @Description WebSocket配置，开启WebSocket支持
 * @Date 2020-07-21 10:50
 * @Author Yan Hua
 * @Version 1.0
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

}