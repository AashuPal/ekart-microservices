package com.infy.ekart.notificationservice.exception;

import com.infy.ekart.notificationservice.dto.response.EmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<EmailResponse> handleException(Exception e) {
        log.error("Error: {}", e.getMessage());
        return ResponseEntity.internalServerError()
            .body(new EmailResponse(false, e.getMessage()));
    }
}