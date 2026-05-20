package com.healthtrack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    // Generic send — auto-detects HTML vs plain text
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            boolean isHtml = body.trim().startsWith("<");
            helper.setText(body, isHtml);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    // Password reset email — called by AuthService
    public void sendPasswordResetEmail(String to, String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
        String subject = "Reset Your HealthTrack Password";
        String body = "<html><body style='font-family:sans-serif;max-width:600px;margin:auto;padding:20px;'>"
            + "<h2 style='color:#22c55e;'>Password Reset Request</h2>"
            + "<p>Click the link below to reset your password. This link expires in 1 hour.</p>"
            + "<a href='" + resetLink + "' style='display:inline-block;padding:12px 24px;"
            + "background:#22c55e;color:white;border-radius:8px;text-decoration:none;"
            + "font-weight:600;margin:16px 0;'>Reset Password</a>"
            + "<p style='color:#94a3b8;font-size:0.85rem;'>If you didn't request this, you can ignore this email.</p>"
            + "</body></html>";
        sendEmail(to, subject, body);
    }
}
