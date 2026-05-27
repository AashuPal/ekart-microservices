package com.infy.ekart.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, String>> handleAuth(AuthException ex) {
        log.error("Auth error: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(Map.of("error", ex.getMessage()));   // changed to "error"
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOther(Exception ex) {
        log.error("Unhandled exception", ex);
        String message = "Internal server error: " + ex.getClass().getSimpleName();
        if (ex.getMessage() != null) {
            message += " - " + ex.getMessage();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", message));
    }
}