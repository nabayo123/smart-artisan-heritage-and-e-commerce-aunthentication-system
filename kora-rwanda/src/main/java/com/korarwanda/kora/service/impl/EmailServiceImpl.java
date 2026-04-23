package com.korarwanda.kora.service.impl;

import com.korarwanda.kora.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("📧 Email sent to {} with subject: {}", to, subject);
        } catch (Exception e) {
            log.error("❌ Failed to send email to {}", to, e);
        }
    }

    @Override
    public void sendVerificationEmail(String to, String code) {
        String htmlContent = String.format(
            "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px;'>" +
            "    <h2 style='color: #2e7d32; text-align: center;'>Welcome to Kora-Rwanda!</h2>" +
            "    <p>Please use the code below to verify your account and start supporting local artisans:</p>" +
            "    <div style='text-align: center; margin: 30px 0;'>" +
            "        <span style='background: #f1f8e9; padding: 15px 30px; border-radius: 5px; font-size: 32px; font-weight: bold; color: #2e7d32; letter-spacing: 5px;'>%s</span>" +
            "    </div>" +
            "    <p style='color: #666; font-size: 13px; text-align: center;'>This code is valid for 10 minutes.</p>" +
            "</div>", code);
        sendHtmlEmail(to, "Verification Code - Kora-Rwanda", htmlContent);
    }

    @Override
    public void sendOrderStatusEmail(String to, String customerName, Long orderId, String status) {
        String htmlContent = String.format(
            "<div style='font-family: sans-serif; padding: 20px; background-color: #f9f9f9;'>" +
            "  <div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>" +
            "    <h2 style='color: #333;'>Hello %s,</h2>" +
            "    <p>Good news! Your order status has been updated.</p>" +
            "    <div style='background-color: #f3f4f6; padding: 15px; border-radius: 6px; margin: 20px 0;'>" +
            "      <p><strong>Order ID:</strong> #%d</p>" +
            "      <p><strong>New Status:</strong> <span style='padding: 4px 8px; background-color: #dcfce7; color: #166534; border-radius: 4px; font-weight: bold;'>%s</span></p>" +
            "    </div>" +
            "    <p>Thank you for choosing Kora-Rwanda.</p>" +
            "    <hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
            "    <p style='color: #999; font-size: 12px;'>Kora-Rwanda Marketplace - Smart Artisan Heritage</p>" +
            "  </div>" +
            "</div>", customerName, orderId, status);
        sendHtmlEmail(to, "Kora-Rwanda Order Update #" + orderId, htmlContent);
    }

    @Override
    public void sendOrderConfirmation(String to, String customerName, Long orderId, BigDecimal amount) {
        String htmlContent = String.format(
            "<div style='font-family: Arial; padding: 20px;'>" +
            "  <h1 style='color: #2e7d32;'>Order Confirmed!</h1>" +
            "  <p>Dear %s, thank you for your purchase.</p>" +
            "  <p>We've received your order <strong>#%d</strong> for a total of <strong>RWF %s</strong>.</p>" +
            "  <p>Our artisans are now preparing your authentic handmade pieces.</p>" +
            "  <div style='margin-top: 20px; padding: 15px; background: #fffde7; border-radius: 5px;'>" +
            "    <p>You can track your order status in your dashboard.</p>" +
            "  </div>" +
            "</div>", customerName, orderId, amount.toString());
        sendHtmlEmail(to, "Confirmation of your Kora-Rwanda Order #" + orderId, htmlContent);
    }
}
