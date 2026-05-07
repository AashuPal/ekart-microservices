package com.infy.ekart.notificationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI notificationServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Notification Service API")
                .description("Email notification service for eKart platform")
                .version("1.0.0")
                .contact(new Contact()
                    .name("eKart Team")
                    .email("support@ekart.com")))
            .servers(List.of(
                new Server().url("http://localhost:8085").description("Local Server")
            ));
    }
}