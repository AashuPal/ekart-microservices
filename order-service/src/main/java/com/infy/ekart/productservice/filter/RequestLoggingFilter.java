package com.infy.ekart.productservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

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
        
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, 0);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        long startTime = System.currentTimeMillis();
        
        log.info("→ REQUEST [{}] {} {} from {}", 
            requestId, request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long timeMs = System.currentTimeMillis() - startTime;
            int status = responseWrapper.getStatus();
            
            String logMessage = "← RESPONSE [{}] {} {} - Status: {} ({}ms)";
            
            if (status >= 500) {
                log.error(logMessage, requestId, request.getMethod(), request.getRequestURI(), status, timeMs);
            } else if (status >= 400) {
                log.warn(logMessage, requestId, request.getMethod(), request.getRequestURI(), status, timeMs);
            } else {
                log.info(logMessage, requestId, request.getMethod(), request.getRequestURI(), status, timeMs);
            }
            
            responseWrapper.copyBodyToResponse();
            MDC.clear();
        }
    }
}