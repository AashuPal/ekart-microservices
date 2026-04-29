package com.infy.ekart.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(String to, String token) {
        String link = "http://localhost:8081/auth/verify?token=" + token;
        
        log.info("========================================");
        log.info("Verification email for: {}", to);
        log.info("Verification link: {}", link);
        log.info("========================================");
        
        // In production, uncomment below:
        // mailSender.send(msg);
    }
}