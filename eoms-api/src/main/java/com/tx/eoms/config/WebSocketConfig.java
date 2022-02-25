package com.tx.eoms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 用于负责处理WebSocket连接。
 * 这个类是多例的，每次有客户端用WebSocket连接这个类，Spring就会创建一个新的WebSocketService对象
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
