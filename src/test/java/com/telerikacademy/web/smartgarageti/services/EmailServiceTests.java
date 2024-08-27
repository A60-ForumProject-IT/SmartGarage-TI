package com.telerikacademy.web.smartgarageti.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTests {
    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void createMailSender_ShouldReturnConfiguredJavaMailSender() {
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) emailService.createMailSender("test@gmail.com", "password");

        assertEquals("smtp.gmail.com", mailSender.getHost());
        assertEquals(587, mailSender.getPort());
        Properties props = mailSender.getJavaMailProperties();
        assertEquals("true", props.get("mail.smtp.auth"));
        assertEquals("true", props.get("mail.smtp.starttls.enable"));
    }
}

