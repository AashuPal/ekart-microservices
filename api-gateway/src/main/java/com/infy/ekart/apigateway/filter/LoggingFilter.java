package com.infy.ekart.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class LoggingFilter {

    @Bean
    public GlobalFilter requestLoggingFilter() {
        return (exchange, chain) -> {
            log.debug("-> {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
            long start = System.currentTimeMillis();
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                long duration = System.currentTimeMillis() - start;
                log.debug("<- {} {} ({}ms)", exchange.getResponse().getStatusCode(),
                        exchange.getRequest().getURI(), duration);
            }));
        };
    }
}