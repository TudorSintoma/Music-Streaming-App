package com.tudor.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Soundwake Password Reset Request");
        message.setText("To reset your password, click the link below:\n" + resetLink);
        message.setFrom("tudor.sintoma@gmail.com");
        System.out.println("Sending reset link...");
        mailSender.send(message);
    }
}
