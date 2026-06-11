package com.phongvu.restapi.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final DynamicAuthorizationInterceptor dynamicAuthorizationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Áp dụng interceptor này cho tất cả các đường dẫn API
        registry.addInterceptor(dynamicAuthorizationInterceptor)
                .addPathPatterns("/api/**"); // Tuỳ chỉnh đường dẫn API
    }
}