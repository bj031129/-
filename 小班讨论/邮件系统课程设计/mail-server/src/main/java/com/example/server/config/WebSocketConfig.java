package com.example.server.config;

import com.example.server.proxy.Pop3Handler;
import com.example.server.proxy.SmtpHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author 曾佳宝
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(getSmtpHandler(), "/smtp").setAllowedOrigins("*");
        webSocketHandlerRegistry.addHandler(getPop3Handler(), "/pop3").setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler getSmtpHandler() {
        return new SmtpHandler();
    }

    @Bean
    WebSocketHandler getPop3Handler() {
        return new Pop3Handler();
    }
}
