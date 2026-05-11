package com.infy.ekart.apigateway.filter;

import com.infy.ekart.apigateway.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GatewayFilterChain chain;

    private AuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new AuthenticationFilter(jwtUtil);
    }

    @Test
    void shouldAllowPublicEndpoint() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/products").build());
        when(chain.filter(any())).thenReturn(Mono.empty());

        filter.apply(new AuthenticationFilter.Config()).filter(exchange, chain);
        verify(chain).filter(exchange);
    }

    @Test
    void shouldRejectRequestWithoutToken() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/orders").build());

        filter.apply(new AuthenticationFilter.Config()).filter(exchange, chain);
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldRejectInvalidToken() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer bad").build());
        when(jwtUtil.validateToken("bad")).thenReturn(false);

        filter.apply(new AuthenticationFilter.Config()).filter(exchange, chain);
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldAllowValidToken() {
        ServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/orders")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer good").build());
        when(jwtUtil.validateToken("good")).thenReturn(true);
        when(chain.filter(any())).thenReturn(Mono.empty());

        filter.apply(new AuthenticationFilter.Config()).filter(exchange, chain);
        assertEquals(HttpStatus.OK, exchange.getResponse().getStatusCode());
    }
}