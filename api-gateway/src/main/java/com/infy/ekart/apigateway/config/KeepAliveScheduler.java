package com.infy.ekart.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.keep-alive.enabled", havingValue = "true", matchIfMissing = false)
public class KeepAliveScheduler {

    private final WebClient webClient;

    @Value("${app.keep-alive.services}")
    private List<String> serviceUrls;

    public KeepAliveScheduler(WebClient.Builder builder) {
        // Hard timeouts so a dead service never blocks the scheduler
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(10))          // max wait for response
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 8000); // max wait to connect

        this.webClient = builder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Scheduled(fixedRateString = "${app.keep-alive.interval-ms:600000}")
    public void pingAllServices() {
        log.info("Keep-alive: pinging {} services...", serviceUrls.size());

        Flux.fromIterable(serviceUrls)
                .flatMap(url -> ping(url + "/actuator/health"))   // all pings fire in parallel
                .subscribe();
    }

    private Mono<Void> ping(String url) {
        return webClient.get()
                .uri(url)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        log.info("Keep-alive OK   [{}] {}", response.statusCode().value(), url);
                    } else {
                        log.warn("Keep-alive WARN [{}] {}", response.statusCode().value(), url);
                    }
                    // consume body so connection is released properly
                    return response.bodyToMono(String.class).then();
                })
                .timeout(Duration.ofSeconds(15))                  // hard ceiling even if Netty hangs
                .doOnError(e -> log.warn("Keep-alive DOWN  [{}] {}", e.getClass().getSimpleName(), url))
                .onErrorComplete();                               // never let one failure kill the Flux
    }
}
