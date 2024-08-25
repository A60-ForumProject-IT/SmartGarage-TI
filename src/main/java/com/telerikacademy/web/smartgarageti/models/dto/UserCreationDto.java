package com.telerikacademy.web.smartgarageti.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreationDto {
    @NotBlank(message = "Username is mandatory")
    @Size(min = 2, max = 20, message = "Username must be between 2 and 20 characters")
    private String username;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(
            regexp = "^\\d{10}$",
            message = "Phone number must be exactly 10 digits"
    )
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String smtpEmail;
    private String smtpPassword;

    public UserCreationDto(String username, String email, String phoneNumber, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.smtpEmail = "ikaragyozov19@gmail.com";
        this.smtpPassword = "xpve hqtn hfcc llwg";
    }
}
