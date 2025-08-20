package com.create.chacha.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

////Cors문제를 해결하기 위해 추가함
// SOP 정책에 의해 다른 origin 접속 불가를 허용 -> global settiong
@Configuration
//@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:*}")
    private List<String> allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로 허용
                .allowedOrigins(allowedOrigins.toArray(new String[0])) // React, Vite에서 접근 허용, String[]으로 변경
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // HTTP 메서드 허용
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowCredentials(true); // 쿠키, 인증 정보 허용
    }
}