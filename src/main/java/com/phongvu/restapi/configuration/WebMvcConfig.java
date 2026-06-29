package com.phongvu.restapi.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final DynamicAuthorizationInterceptor dynamicAuthorizationInterceptor;
    private final RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/v1/identity/authenticate");
                
        // Áp dụng interceptor này cho tất cả các đường dẫn API
        registry.addInterceptor(dynamicAuthorizationInterceptor)
                .addPathPatterns("/api/v1/**"); // Tuỳ chỉnh đường dẫn API
    }
}