package com.infy.ekart.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("auth-service", r -> r.path("/auth/**").uri("http://localhost:8081"))
            .route("cart-service", r -> r.path("/api/v1/carts/**").uri("http://localhost:8082"))
            .route("product-service", r -> r.path("/api/v1/products/**", "/api/v1/categories/**", "/api/v1/brands/**").uri("http://localhost:8083"))
            .route("payment-service", r -> r.path("/api/v1/payments/**").uri("http://localhost:8084"))
            .route("notification-service", r -> r.path("/api/v1/email/**").uri("http://localhost:8085"))
            .route("order-service", r -> r.path("/api/v1/orders/**").uri("http://localhost:8086"))
            .build();
    }
}