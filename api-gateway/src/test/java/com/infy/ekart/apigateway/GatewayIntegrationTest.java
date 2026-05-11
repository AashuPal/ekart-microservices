package com.infy.ekart.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldRoutePublicProductEndpoint() {
        webTestClient.get().uri("/api/v1/products")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void shouldBlockUnauthenticatedOrderAccess() {
        webTestClient.get().uri("/api/v1/orders")
                .exchange()
                .expectStatus().isEqualTo(401);
    }

    @Test
    void shouldAllowLoginWithoutToken() {
        webTestClient.post().uri("/auth/login")
                .exchange()
                .expectStatus().is2xxSuccessful();
    }
}