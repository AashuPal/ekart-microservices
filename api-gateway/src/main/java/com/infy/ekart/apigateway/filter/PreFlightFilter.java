package com.infy.ekart.apigateway.filter;

import com.infy.ekart.apigateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

//@Component
//@Slf4j
public class PreFlightFilter extends AbstractGatewayFilterFactory<PreFlightFilter.Config> {

    private final JwtUtil jwtUtil;

    public PreFlightFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();
            String method = exchange.getRequest().getMethod().name();
            log.debug("Auth filter checking path: {} {}", method, path);

            // 1. Completely public paths – no token needed
            if (path.startsWith("/auth/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/webjars")) {
                log.debug("Public path – forwarding without token: {}", path);
                return chain.filter(exchange);
            }

            // 2. Public READ‑ONLY product endpoints – allow without token
            if (("GET".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method)) &&
                (path.startsWith("/api/v1/products") ||
                 path.startsWith("/api/v1/categories") ||
                 path.startsWith("/api/v1/brands"))) {
                log.debug("Public GET – forwarding without token: {}", path);
                return chain.filter(exchange);
            }

            // 3. All other requests (write operations, orders, cart, etc.) require a valid token
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authHeader.substring(7);
            if (!jwtUtil.validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // 4. Coarse‑grained authorization: admin‑only paths
            String role = jwtUtil.getRoleFromToken(token);
            String email = jwtUtil.getUsernameFromToken(token);

            // Define admin‑only write paths
            boolean isAdminWritePath = path.startsWith("/api/v1/products") ||
                                       path.startsWith("/api/v1/categories") ||
                                       path.startsWith("/api/v1/brands") ||
                                       path.startsWith("/api/v1/reviews");

            if (isAdminWritePath && !"ROLE_ADMIN".equalsIgnoreCase(role)) {
                log.warn("Non‑admin user {} tried to access admin write path: {}", email, path);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // 5. Forward user info headers to downstream microservices
            exchange.getRequest().mutate()
                    .header("X-User-Email", email)
                    .header("X-User-Role", role)
                    .build();

            return chain.filter(exchange);
        };
    }

    public static class Config {}
}