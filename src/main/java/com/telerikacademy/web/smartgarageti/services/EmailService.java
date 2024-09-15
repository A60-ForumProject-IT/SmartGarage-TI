package com.telerikacademy.web.smartgarageti.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Properties;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String defaultFromEmail;

    @Value("${spring.mail.password}")
    private String defaultSmtpPassword;

    // Съществуващ метод за изпращане на имейл с 3 аргумента
    public void sendEmail(String to, String subject, String text) {
        JavaMailSender mailSender = createMailSender(defaultFromEmail, defaultSmtpPassword);
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(defaultFromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendEmail(String to, String subject, String text, String from) {
        JavaMailSender mailSender = createMailSender(defaultFromEmail, defaultSmtpPassword);
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            helper.setReplyTo(from);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendEmailWithAttachment(String to, String subject, String text, String from, MultipartFile pdfFile) {
        JavaMailSender mailSender = createMailSender(defaultFromEmail, defaultSmtpPassword);
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            helper.setReplyTo(from);

            helper.addAttachment(pdfFile.getOriginalFilename(), pdfFile);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    JavaMailSender createMailSender(String from, String smtpPassword) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties props = mailSender.getJavaMailProperties();

        if (from.endsWith("@gmail.com")) {
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
