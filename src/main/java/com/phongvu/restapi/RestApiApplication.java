package com.phongvu.restapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync  // I6 Fix: Enables @Async on MailServiceImpl so email sending doesn't block request threads
@OpenAPIDefinition(info = @Info(title = "Enterprise REST API", version = "v1.0", description = "Tài liệu tích hợp API cho hệ thống"))
public class RestApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestApiApplication.class, args);
    }
}
