package com.firomsa.ecommerce.v1.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.firomsa.ecommerce.config.MailConfig;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {
    private final JavaMailSender emailSender;
    private final MailConfig mailConfig;

    public EmailService(JavaMailSender emailSender, MailConfig mailConfig) {
        this.emailSender = emailSender;
        this.mailConfig = mailConfig;
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailConfig.getEmail());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
        log.info("Mail sent successfully");
    }

}
