package com.infy.ekart.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

//@Component
public class JwtEmailHeaderFilter extends AbstractGatewayFilterFactory<Object> {

    private final javax.crypto.SecretKey signingKey;

    public JwtEmailHeaderFilter(@Value("${spring.security.jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    Claims claims = Jwts.parser()
                            .verifyWith(signingKey)          // <-- new API
                            .build()
                            .parseSignedClaims(token)        // parseSignedClaims, not parseClaimsJws
                            .getPayload();                   // getPayload(), not getBody()

                    String email = claims.getSubject();      // or claims.get("email", String.class)
                    if (email != null) {
                        exchange = exchange.mutate()
                                .request(r -> r.header("X-User-Email", email))
                                .build();
                    }
                } catch (Exception e) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
            }
            return chain.filter(exchange);
        };
    }
}