package com.myTMS.demo.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageSourceConfig {

    /**
     * 각종 메시지를 국제화 하기 위한 properties 를 등록하는 메시지 소스
     * 기존에 등록된 메시지소스를 사용하지 않기 위해서 @Primary 어노테이션 사용
     */

    @Bean
    @Primary
    public MessageSource messageSource(){
        ResourceBundleMessageSource resource = new ResourceBundleMessageSource();
        resource.setBasenames("message/messages",
                "message/errors");
        resource.setDefaultEncoding("UTF-8");

        return resource;
    }
}
