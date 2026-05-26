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

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.base-url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Verification link (no hardcoded URL)
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

    // Password reset (no hardcoded URL)
    public void sendPasswordResetEmail(String to, String resetToken) {
        String link = baseUrl + "/reset-password?token=" + resetToken;
        sendHtml(to, "Reset your eKart password",
                "<p>Click <a href='" + link + "'>here</a> to reset your password.</p>");
    }

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            helper.setFrom(from);
            mailSender.send(msg);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}