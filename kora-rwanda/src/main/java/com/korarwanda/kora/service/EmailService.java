package com.korarwanda.kora.service;

public interface EmailService {
    void sendHtmlEmail(String to, String subject, String htmlContent);
    void sendVerificationEmail(String to, String code);
    void sendOrderStatusEmail(String to, String customerName, Long orderId, String status);
    void sendOrderConfirmation(String to, String customerName, Long orderId, java.math.BigDecimal amount);
}
