package com.myTMS.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myTMS.demo.handler.MyWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Configuration
public class GeneralConfig {

    /**
     * 각종 빈 등록
     * 시리얼라이징과 디시리얼라이징을 위한 ObjectMapper
     * 비밀번호 암호화를 위한 PasswordEncoder
     * 외부 API 호출을 위한 RestTemplate (Kakao Map API)
     */

    @Bean
    public ObjectMapper objectMapper(){
        return new Jackson2ObjectMapperBuilder().build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
