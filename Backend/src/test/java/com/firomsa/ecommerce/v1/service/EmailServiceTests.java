package com.firomsa.ecommerce.v1.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import com.firomsa.ecommerce.config.MailConfig;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class EmailServiceTests {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MailConfig mailConfig;

    @InjectMocks
    private EmailService emailService;

    @Test
    public void EmailService_SendSimpleMessage_SendsMail() {
        // Arrange
        org.mockito.BDDMockito.given(mailConfig.getEmail()).willReturn("no-reply@example.com");

        // Act
        emailService.sendSimpleMessage("to@example.com", "Subject", "Body");

        // Assert
        verify(mailSender, times(1)).send(org.mockito.ArgumentMatchers.any(SimpleMailMessage.class));
    }
}
