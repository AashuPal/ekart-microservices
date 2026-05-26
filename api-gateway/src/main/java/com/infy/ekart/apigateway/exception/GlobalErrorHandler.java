package com.infy.ekart.apigateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.stereotype.Component;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = getHttpStatus(exchange, ex);

        log.error("Gateway error [{}]: {}", status.value(), ex.getMessage());

        exchange.getResponse().setStatusCode(status);
        String body = String.format("{\"error\":\"%s\"}", status.getReasonPhrase());
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private HttpStatus getHttpStatus(ServerWebExchange exchange, Throwable ex) {
        HttpStatusCode code = exchange.getResponse().getStatusCode();
        if (code != null) {
            // Convert to HttpStatus safely
            HttpStatus status = HttpStatus.resolve(code.value());
            if (status != null) return status;
            // Fallback: use the code as a default HttpStatus
            return HttpStatus.valueOf(code.value());
        }
        // Fallback to raw status if set
        Integer rawStatus = exchange.getResponse().getRawStatusCode();
        if (rawStatus != null && rawStatus > 0) {
            HttpStatus status = HttpStatus.resolve(rawStatus);
            if (status != null) return status;
        }
        // Backend connection failure
        if (ex instanceof ConnectException) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
        // Default unknown error
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}