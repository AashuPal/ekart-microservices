package com.infy.ekart.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class EmailService {

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    @Value("${sendgrid.from-name}")
    private String fromName;
    
    @Value("${sendgrid.api-key}")
    private String apiKey;

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
        String html = String.format("""
            <div style="font-family: Arial, sans-serif; padding: 20px;">
                <h2>Reset your eKart Password</h2>
                <p>Click the button below to reset your password:</p>
                <a href="%s" style="display: inline-block; padding: 12px 24px; background: #dc2626; color: white; text-decoration: none; border-radius: 8px; font-weight: bold;">
                    Reset Password
                </a>
                <p style="margin-top: 20px; color: #666;">Or copy this link: %s</p>
                <p>This link expires in 10 minutes. If you did not request this, ignore this email.</p>
            </div>
            """, link, link);
        sendHtml(to, "Reset your eKart password", html);
    }

    private void sendHtml(String to, String subject, String htmlContent) {
        try {
            // Create SendGrid instance directly with API key
            SendGrid sendGrid = new SendGrid(apiKey);
            
            Email from = new Email(fromEmail, fromName);
            Email recipient = new Email(to);
            Content content = new Content("text/html", htmlContent);
            Mail mail = new Mail(from, subject, recipient, content);
            
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sendGrid.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Email sent successfully to {} - Status code: {}", to, response.getStatusCode());
            } else {
                log.error("Failed to send email to {} - Status: {}, Body: {}", 
                    to, response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to send email. Status code: " + response.getStatusCode());
            }
        } catch (IOException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Could not send email, please try again later.", e);
        }
    }
                }
