package com.sda.config;

import com.sda.connection.interceptor.WebSocketInterceptor;
import com.sda.connection.websocket.WebSocketHandlerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    WebSocketHandlerImpl webSocketHandler;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(webSocketHandler, "/api/webssh/{userId}")
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("*");
    }
}