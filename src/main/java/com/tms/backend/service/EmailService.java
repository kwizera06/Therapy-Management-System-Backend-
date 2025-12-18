package com.tms.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;
    
    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = frontendUrl + "/reset-password/" + token;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset Request - Therapy Management System");
        message.setText("Hello,\n\n" +
                "You have requested to reset your password. Please click the link below to reset your password:\n\n" +
                resetLink + "\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you did not request this password reset, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Therapy Management System Team");
        
        mailSender.send(message);
    }
    
    public void send2FACode(String toEmail, String code, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Your 2FA Verification Code - Therapy Management System");
        message.setText("Hello " + userName + ",\n\n" +
                "Your two-factor authentication code is:\n\n" +
                code + "\n\n" +
                "This code will expire in 10 minutes.\n\n" +
                "If you did not attempt to log in, please secure your account immediately.\n\n" +
                "Best regards,\n" +
                "Therapy Management System Team");
        
        mailSender.send(message);
    }
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
