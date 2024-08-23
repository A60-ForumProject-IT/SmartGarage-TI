package com.telerikacademy.web.smartgarageti.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import java.util.Properties;

@Service
public class EmailService {

    public void sendEmail(String from, String smtpPassword, String to, String subject, String text) {
        JavaMailSender mailSender = createMailSender(from, smtpPassword);
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private JavaMailSender createMailSender(String from, String smtpPassword) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties props = mailSender.getJavaMailProperties();

        if (from.endsWith("@abv.bg")) {
            mailSender.setHost("smtp.abv.bg");
            mailSender.setPort(465);
            mailSender.setUsername(from);
            mailSender.setPassword(smtpPassword);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
        } else if (from.endsWith("@gmail.com")) {
            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(587);
            mailSender.setUsername(from);
            mailSender.setPassword(smtpPassword);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
        }

        return mailSender;
    }

}