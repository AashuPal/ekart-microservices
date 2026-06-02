package com.infy.ekart.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
@Slf4j
public class EmailService {

    private final RestTemplate restTemplate;

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.api-url:https://api.brevo.com/v3/smtp/email}")
    private String apiUrl;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username:noreply@ekart.com}")   // verified sender email
    private String fromEmail;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendVerificationLink(String to, String name, String token) {
        String link = baseUrl + "/verify?token=" + token;
        String html = String.format("""
            <div style="font-family: Arial, sans-serif; padding: 20px;">
                <h2>Welcome to eKart, %s!</h2>
                <p>Click the button below to verify your email address:</p>
                <a href="%s" style="display: inline-block; padding: 12px 24px; background: #2563eb; color: white; text-decoration: none; border-radius: 8px; font-weight: bold;">
                    Verify Email
                </a>
                <p style="margin-top: 20px; color: #666;">Or copy this link: %s</p>
                <p>This link expires in 10 minutes.</p>
            </div>
            """, name, link, link);
        sendHtml(to, "Verify your eKart email", html);
    }

    public void sendPasswordResetEmail(String to, String resetToken) {
        String link = baseUrl + "/reset-password?token=" + resetToken;
        String html = "<p>Click <a href='" + link + "'>here</a> to reset your password.</p>";
        sendHtml(to, "Reset your eKart password", html);
    }

    private void sendHtml(String to, String subject, String htmlContent) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);   // Brevo API key header

            Map<String, Object> body = new HashMap<>();
            
            // Sender
            Map<String, String> sender = new HashMap<>();
            sender.put("email", fromEmail);
            sender.put("name", "eKart");
            body.put("sender", sender);

            // Recipient(s)
            List<Map<String, String>> toList = new ArrayList<>();
            Map<String, String> recipient = new HashMap<>();
            recipient.put("email", to);
            toList.add(recipient);
            body.put("to", toList);

            body.put("subject", subject);
            body.put("htmlContent", htmlContent);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiUrl,
                    new HttpEntity<>(body, headers),
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                log.info("Email sent successfully to {}", to);
            } else {
                log.error("Brevo API returned non-success: {} - {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Email sending failed");
            }
        } catch (Exception e) {
            log.error("Failed to send email via Brevo API: {}", e.getMessage());
            throw new RuntimeException("Could not send email, please try again later.");
        }
    }
}
