package com.telerikacademy.web.smartgarageti.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreationDto {
    private String username;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;

    // Нови полета за SMTP
    private String smtpEmail;
    private String smtpPassword;

    public UserCreationDto(String username, String email, String phoneNumber, String firstName, String lastName, String smtpEmail, String smtpPassword) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.smtpEmail = smtpEmail;
        this.smtpPassword = smtpPassword;
    }
}
