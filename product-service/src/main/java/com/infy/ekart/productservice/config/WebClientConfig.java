package com.infy.ekart.productservice.config;  // ✅ Fixed: info → infy

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {  // ✅ Fixed: WebclientConfig → WebClientConfig

    @Bean
    public WebClient.Builder webClientBuilder() {  // ✅ Fixed: Webclient → WebClient
        return WebClient.builder();  // ✅ Fixed: Webclient → WebClient
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {  // ✅ Fixed: Webclient → WebClient
        return builder.build();
    }
}