package com.infy.ekart.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // API Information
                .info(new Info()
                        .title("Auth Service API")
                        .version("1.0.0")
                        .description("""
                                Microservice responsible for authentication and authorization.
                                
                                **Features:**
                                - User registration and login
                                - JWT token generation and validation
                                - OAuth2 integration (Google/GitHub)
                                - OTP generation and email verification
                                - Password reset flow
                                - Redis-based token/OTP caching
                                """)
                        .contact(new Contact()
                                .name("Infy Ekart Team")
                                .email("support@infyekart.com")
                                .url("https://infyekart.com"))
                        .license(new License()
                                .name("Internal Use Only")
                                .url("https://infyekart.com/license")))

                // Servers (different environments)
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Local Development Server"),
                        new Server()
                                .url("http://auth-service:8081")
                                .description("Docker Internal (Eureka Service Name)")
                ))

                // Global Security Scheme (JWT Bearer Token)
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("""
                                                Enter your JWT token without the 'Bearer ' prefix.
                                                
                                                **How to get a token:**
                                                1. Call the **POST /auth/login** endpoint
                                                2. Copy the `accessToken` from the response
                                                3. Paste it here and click **Authorize**
                                                """)));
    }
}