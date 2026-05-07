package com.infy.ekart.cartservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI cartServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Cart Service API")
                .description("Microservice for managing shopping carts in eKart platform")
                .version("1.0.0")
                .contact(new Contact()
                    .name("eKart Team")
                    .email("support@ekart.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}