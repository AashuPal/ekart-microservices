package com.infy.ekart.notificationservice.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private SendGrid sendGrid;

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from-name}")
    private String fromName;

    @Value("${app.base-url}")
    private String baseUrl;

    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isBlank()) {
            log.error("SendGrid API key is not configured!");
            throw new IllegalStateException("SendGrid API key is required");
        }
        this.sendGrid = new SendGrid(apiKey);
        log.info("SendGrid email service initialized successfully");
    }

    // ============ ORDER CONFIRMATION ============
    public boolean sendOrderConfirmation(String to, String customerName, String orderNumber,
                                          String orderTotal, String itemList, String paymentMethod, String trackingNumber) {
        String subject = "✅ Order Confirmed! #" + orderNumber;
        String html = """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0;">
                <div style="max-width: 600px; margin: 20px auto; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,.1);">
                    <div style="background: linear-gradient(135deg, #22c55e, #16a34a); padding: 30px; text-align: center;">
                        <h1 style="color: #fff; margin: 0; font-size: 24px;">✅ Order Confirmed!</h1>
                    </div>
                    <div style="padding: 30px;">
                        <h2 style="color: #333;">Hi %s 👋</h2>
                        <p style="color: #555;">Your order has been placed successfully!</p>
                        <div style="background: #f0fdf4; border-left: 4px solid #22c55e; padding: 15px; margin: 20px 0; border-radius: 8px;">
                            <p><strong>Order Number:</strong> #%s</p>
                            <p><strong>Items:</strong><br>%s</p>
                            <p><strong>Total Amount:</strong> ₹%s</p>
                            <p><strong>Payment:</strong> %s</p>
                            <p><strong>Tracking:</strong> %s</p>
                        </div>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s/order-tracking/%s" style="background: #22c55e; color: #fff; padding: 12px 30px; text-decoration: none; border-radius: 8px; font-weight: bold;">
                                📦 Track Your Order
                            </a>
                        </div>
                    </div>
                    <div style="background: #f5f5f5; padding: 15px; text-align: center; font-size: 12px; color: #888;">
                        <p>eKart © 2026 | support@ekart.com</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(customerName != null ? customerName : "Customer",
                         orderNumber,
                         itemList != null ? itemList.replace(",", "<br>• ") : "N/A",
                         orderTotal,
                         paymentMethod != null ? paymentMethod : "N/A",
                         trackingNumber != null ? trackingNumber : "Pending",
                         baseUrl, orderNumber);

        return sendHtmlEmail(to, subject, html);
    }

    // ============ ORDER STATUS UPDATE ============
    public boolean sendOrderStatusUpdate(String to, String customerName, String orderNumber,
                                          String status, String trackingNumber) {
        String subject = "📦 Order " + status + " - #" + orderNumber;
        String color = switch (status.toUpperCase()) {
            case "SHIPPED" -> "linear-gradient(135deg, #f59e0b, #d97706)";
            case "OUT_FOR_DELIVERY" -> "linear-gradient(135deg, #3b82f6, #2563eb)";
            case "DELIVERED" -> "linear-gradient(135deg, #22c55e, #16a34a)";
            default -> "linear-gradient(135deg, #6b7280, #4b5563)";
        };

        String displayStatus = status.substring(0, 1).toUpperCase() + 
                               status.substring(1).toLowerCase().replace("_", " ");

        String html = """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0;">
                <div style="max-width: 600px; margin: 20px auto; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,.1);">
                    <div style="background: %s; padding: 30px; text-align: center;">
                        <h1 style="color: #fff; margin: 0; font-size: 24px;">📦 Order %s!</h1>
                    </div>
                    <div style="padding: 30px;">
                        <h2 style="color: #333;">Hi %s 👋</h2>
                        <p style="color: #555;">Your order <strong>#%s</strong> status has been updated.</p>
                        <div style="background: #f8fafc; border-left: 4px solid #3b82f6; padding: 15px; margin: 20px 0; border-radius: 8px;">
                            <p><strong>Tracking Number:</strong> %s</p>
                        </div>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s/order-tracking/%s" style="background: #3b82f6; color: #fff; padding: 12px 30px; text-decoration: none; border-radius: 8px; font-weight: bold;">
                                📦 Track Order
                            </a>
                        </div>
                    </div>
                    <div style="background: #f5f5f5; padding: 15px; text-align: center; font-size: 12px; color: #888;">
                        <p>eKart © 2026 | support@ekart.com</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(color, displayStatus,
                         customerName != null ? customerName : "Customer",
                         orderNumber,
                         trackingNumber != null ? trackingNumber : "N/A",
                         baseUrl, orderNumber);

        return sendHtmlEmail(to, subject, html);
    }

    // ============ PAYMENT CONFIRMATION ============
    public boolean sendPaymentConfirmation(String to, String customerName, String orderNumber,
                                            String orderTotal, String paymentMethod, String transactionId) {
        String subject = "💰 Payment Confirmed - #" + orderNumber;
        String html = """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0;">
                <div style="max-width: 600px; margin: 20px auto; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,.1);">
                    <div style="background: linear-gradient(135deg, #8b5cf6, #7c3aed); padding: 30px; text-align: center;">
                        <h1 style="color: #fff; margin: 0; font-size: 24px;">💰 Payment Successful!</h1>
                    </div>
                    <div style="padding: 30px;">
                        <h2 style="color: #333;">Hi %s 👋</h2>
                        <p style="color: #555;">Your payment has been received!</p>
                        <div style="background: #f5f3ff; border-left: 4px solid #8b5cf6; padding: 15px; margin: 20px 0; border-radius: 8px;">
                            <p><strong>Order Number:</strong> #%s</p>
                            <p><strong>Amount:</strong> ₹%s</p>
                            <p><strong>Method:</strong> %s</p>
                            <p><strong>Transaction ID:</strong> %s</p>
                        </div>
                    </div>
                    <div style="background: #f5f5f5; padding: 15px; text-align: center; font-size: 12px; color: #888;">
                        <p>eKart © 2026 | support@ekart.com</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(customerName != null ? customerName : "Customer",
                         orderNumber, orderTotal,
                         paymentMethod != null ? paymentMethod : "N/A",
                         transactionId != null ? transactionId : "N/A");

        return sendHtmlEmail(to, subject, html);
    }

    // ============ REFUND CONFIRMATION ============
    public boolean sendRefundConfirmation(String to, String customerName, String orderNumber,
                                           String refundAmount, String refundReason) {
        String subject = "↩️ Refund Processed - #" + orderNumber;
        String html = """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0;">
                <div style="max-width: 600px; margin: 20px auto; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,.1);">
                    <div style="background: linear-gradient(135deg, #ef4444, #dc2626); padding: 30px; text-align: center;">
                        <h1 style="color: #fff; margin: 0; font-size: 24px;">↩️ Refund Processed</h1>
                    </div>
                    <div style="padding: 30px;">
                        <h2 style="color: #333;">Hi %s 👋</h2>
                        <p style="color: #555;">A refund has been processed for your order.</p>
                        <div style="background: #fef2f2; border-left: 4px solid #ef4444; padding: 15px; margin: 20px 0; border-radius: 8px;">
                            <p><strong>Order Number:</strong> #%s</p>
                            <p><strong>Refund Amount:</strong> ₹%s</p>
                            <p><strong>Reason:</strong> %s</p>
                        </div>
                        <p style="color: #888; font-size: 14px;">The amount will be credited within 5-7 business days.</p>
                    </div>
                    <div style="background: #f5f5f5; padding: 15px; text-align: center; font-size: 12px; color: #888;">
                        <p>eKart © 2026 | support@ekart.com</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(customerName != null ? customerName : "Customer",
                         orderNumber, refundAmount,
                         refundReason != null ? refundReason : "Customer request");

        return sendHtmlEmail(to, subject, html);
    }

    // ============ VERIFICATION EMAIL ============
    public boolean sendVerificationLink(String to, String name, String token) {
        String link = baseUrl + "/verify?token=" + token;
        String subject = "Verify your eKart email";
        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0;">
                <div style="max-width: 600px; margin: 20px auto; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,.1);">
                    <div style="background: linear-gradient(135deg, #2563eb, #1d4ed8); padding: 30px; text-align: center;">
                        <h1 style="color: #fff; margin: 0; font-size: 24px;">Welcome to eKart!</h1>
                    </div>
                    <div style="padding: 30px;">
                        <h2 style="color: #333;">Hi %s 👋</h2>
                        <p style="color: #555;">Click the button below to verify your email address:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background: #2563eb; color: #fff; padding: 12px 30px; text-decoration: none; border-radius: 8px; font-weight: bold;">
                                Verify Email
                            </a>
                        </div>
                        <p style="color: #666; font-size: 14px;">Or copy this link: %s</p>
                        <p style="color: #888; font-size: 12px;">This link expires in 10 minutes.</p>
                    </div>
                    <div style="background: #f5f5f5; padding: 15px; text-align: center; font-size: 12px; color: #888;">
                        <p>eKart © 2026 | support@ekart.com</p>
                    </div>
                </div>
            </body>
            </html>
            """, name != null ? name : "Customer", link, link);
        
        return sendHtmlEmail(to, subject, html);
    }

    // ============ PASSWORD RESET EMAIL ============
    public boolean sendPasswordResetEmail(String to, String resetToken) {
        String link = baseUrl + "/reset-password?token=" + resetToken;
        String subject = "Reset your eKart password";
        String html = String.format("""
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0;">
                <div style="max-width: 600px; margin: 20px auto; background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,.1);">
                    <div style="background: linear-gradient(135deg, #dc2626, #b91c1c); padding: 30px; text-align: center;">
                        <h1 style="color: #fff; margin: 0; font-size: 24px;">Reset Your Password</h1>
                    </div>
                    <div style="padding: 30px;">
                        <p style="color: #555;">Click the button below to reset your password:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background: #dc2626; color: #fff; padding: 12px 30px; text-decoration: none; border-radius: 8px; font-weight: bold;">
                                Reset Password
                            </a>
                        </div>
                        <p style="color: #666; font-size: 14px;">Or copy this link: %s</p>
                        <p style="color: #888; font-size: 12px;">This link expires in 10 minutes. If you did not request this, ignore this email.</p>
                    </div>
                    <div style="background: #f5f5f5; padding: 15px; text-align: center; font-size: 12px; color: #888;">
                        <p>eKart © 2026 | support@ekart.com</p>
                    </div>
                </div>
            </body>
            </html>
            """, link, link);
        
        return sendHtmlEmail(to, subject, html);
    }

    // ============ GENERIC EMAIL ============
    public boolean sendEmail(String to, String subject, String htmlBody) {
        return sendHtmlEmail(to, subject, htmlBody);
    }

    // ============ HELPER ============
    private boolean sendHtmlEmail(String to, String subject, String html) {
        // Validate inputs
        if (to == null || to.isBlank()) {
            log.error("Cannot send email: recipient address is null or empty");
            return false;
        }
        
        if (subject == null || subject.isBlank()) {
            log.error("Cannot send email to {}: subject is null or empty", to);
            return false;
        }
        
        if (html == null || html.isBlank()) {
            log.error("Cannot send email to {}: HTML content is null or empty", to);
            return false;
        }
        
        try {
            Email from = new Email(fromEmail, fromName);
            Email recipient = new Email(to);
            Content content = new Content("text/html", html);
            Mail mail = new Mail(from, subject, recipient, content);
            
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sendGrid.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("✅ Email sent to: {} | Subject: {} | Status: {}", 
                         to, subject, response.getStatusCode());
                return true;
            } else {
                log.error("❌ Failed to send email to {} | Status: {} | Body: {}", 
                         to, response.getStatusCode(), response.getBody());
                return false;
            }
        } catch (IOException e) {
            log.error("❌ Failed to send email to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }
                                  }
