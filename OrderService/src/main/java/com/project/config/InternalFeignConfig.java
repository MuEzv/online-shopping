package com.project.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalFeignConfig {
    private static final String INTERNAL_TOKEN = "Internal my-internal-secret-token";

    @Bean
    public RequestInterceptor internalRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", INTERNAL_TOKEN);
        };
    }
}