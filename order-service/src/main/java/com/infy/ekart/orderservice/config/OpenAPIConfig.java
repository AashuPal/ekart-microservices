package com.infy.ekart.orderservice.config; // Change package per service

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Order Service API")  // Change per service
                .description("Microservice for managing orders in eKart platform")
                .version("1.0.0")
                .contact(new Contact()
                    .name("eKart Team")
                    .email("support@ekart.com")
                    .url("https://ekart.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(
                new Server().url("http://localhost:8086").description("Local Server")  // Change port
            ));
    }
}