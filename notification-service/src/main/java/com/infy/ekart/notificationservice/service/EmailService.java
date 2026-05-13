package com.infy.ekart.notificationservice.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from-name}")
    private String fromName;

    @Value("${app.base-url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ============ ORDER CONFIRMATION ============
    public boolean sendOrderConfirmation(String to, String customerName, String orderNumber,
                                          String orderTotal, String itemList, String trackingNumber) {
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
                         trackingNumber != null ? trackingNumber : "N/A",
                         trackingNumber != null ? trackingNumber : "Pending",
                         baseUrl, orderNumber);

        return sendHtmlEmail(to, subject, html);
    }

    // ============ ORDER STATUS UPDATE ============
    public boolean sendOrderStatusUpdate(String to, String customerName, String orderNumber,
                                          String status, String trackingNumber) {
        String subject = "📦 Order " + status + " - #" + orderNumber;
        String color = switch (status) {
            case "SHIPPED" -> "linear-gradient(135deg, #f59e0b, #d97706)";
            case "OUT_FOR_DELIVERY" -> "linear-gradient(135deg, #3b82f6, #2563eb)";
            case "DELIVERED" -> "linear-gradient(135deg, #22c55e, #16a34a)";
            default -> "linear-gradient(135deg, #6b7280, #4b5563)";
        };

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
            """.formatted(color, status,
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

    // ============ GENERIC EMAIL ============
    public boolean sendEmail(String to, String subject, String htmlBody) {
        return sendHtmlEmail(to, subject, htmlBody);
    }

    // ============ HELPER ============
    private boolean sendHtmlEmail(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            helper.setFrom(fromEmail, fromName);
            mailSender.send(message);
            log.info("✅ Email sent to: {} | Subject: {}", to, subject);
            return true;
        } catch (Exception e) {
            log.error("❌ Failed to send email to {}: {}", to, e.getMessage());
            return false;
        }
    }
}