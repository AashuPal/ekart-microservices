package com.infy.ekart.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.keep-alive.enabled", havingValue = "true", matchIfMissing = false)
public class KeepAliveScheduler {

    private final WebClient webClient;
    private final KeepAliveProperties keepAliveProperties;

    public KeepAliveScheduler(WebClient.Builder builder, KeepAliveProperties keepAliveProperties) {
        this.webClient = builder.build();
        this.keepAliveProperties = keepAliveProperties;
    }

    @Scheduled(fixedRateString = "${app.keep-alive.interval-ms:600000}")
    public void pingAllServices() {
        Flux.fromIterable(keepAliveProperties.getServices())
            .flatMap(url -> ping(url + "/actuator/health"))
            .subscribe();
    }

    private Mono<Void> ping(String url) {
        return webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(String.class)
            .doOnSuccess(r -> log.debug("Keep-alive OK: {}", url))
            .doOnError(e -> log.warn("Keep-alive FAILED: {} — {}", url, e.getMessage()))
            .onErrorComplete()
            .then();
    }
}