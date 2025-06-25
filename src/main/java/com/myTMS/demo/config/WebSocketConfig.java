package com.myTMS.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.handler.MyWebSocketHandler;
import com.myTMS.demo.interceptor.WebSocketThrottleInterceptor;
import com.myTMS.demo.service.MessageService;
import com.myTMS.demo.service.RedisService;
import com.myTMS.demo.service.localrep.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final MessageService messageService;
    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(myWebSocketHandler(), "/ws/chat")
                .addInterceptors(new HttpSessionHandshakeInterceptor(),
                        new WebSocketThrottleInterceptor())

                .setAllowedOrigins("*");
    }

    @Bean
    public TextWebSocketHandler myWebSocketHandler() {
        return new MyWebSocketHandler(messageService, chatService, objectMapper, redisService);
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer(){
        ServletServerContainerFactoryBean factoryBean = new ServletServerContainerFactoryBean();
        factoryBean.setMaxTextMessageBufferSize(64 * 1024); // 64 KB
        factoryBean.setMaxBinaryMessageBufferSize(512 * 1024);// 512 KB
        return factoryBean;
    }
}
