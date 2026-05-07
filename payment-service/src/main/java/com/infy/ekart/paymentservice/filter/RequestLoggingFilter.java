package com.infy.ekart.paymentservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);

        long startTime = System.currentTimeMillis();

        log.info("→ REQUEST [{}] {} {}", requestId, request.getMethod(), request.getRequestURI());

        try {
            filterChain.doFilter(request, response);
        } finally {
            long timeMs = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            String logMsg = "← RESPONSE [{}] {} {} - {} ({}ms)";
            if (status >= 500) {
                log.error(logMsg, requestId, request.getMethod(), request.getRequestURI(), status, timeMs);
            } else if (status >= 400) {
                log.warn(logMsg, requestId, request.getMethod(), request.getRequestURI(), status, timeMs);
            } else {
                log.info(logMsg, requestId, request.getMethod(), request.getRequestURI(), status, timeMs);
            }
            MDC.clear();
        }
    }
}