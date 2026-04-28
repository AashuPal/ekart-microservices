package com.infy.ekart.productservice.config;  // ✅ Fixed: infey → infy

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI productServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Product Service API")
                .description("Microservice for managing products in eKart platform")
                .version("1.0.0")
                .contact(new Contact()
                    .name("eKart Team")
                    .email("support@ekart.com")));
    }
}