package com.infy.ekart.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
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
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, "eKart");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new RuntimeException("Could not send email, please try again later.");
        }
    }
}
