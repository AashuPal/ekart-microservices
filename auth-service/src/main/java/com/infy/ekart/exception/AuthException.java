package com.infy.ekart.exception;

import org.springframework.stereotype.Component;


public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
    public AuthException(String message, Throwable cause) {
        super(message, cause);          // preserves stack trace
    }
}

// Similarly: UserNotFoundException, InvalidTokenException, etc.