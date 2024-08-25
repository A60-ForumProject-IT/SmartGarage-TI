package com.telerikacademy.web.smartgarageti.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDto {
    @NotBlank(message = "Password is mandatory")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[+\\-*/^@#$%!&]).{8,}$",
            message = "Password must contain at least 8 characters, including one uppercase letter, one digit, and one special symbol (+, -, *, ^, etc.)"
    )
    private String oldPassword;
    @NotBlank(message = "Password is mandatory")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[+\\-*/^@#$%!&]).{8,}$",
            message = "Password must contain at least 8 characters, including one uppercase letter, one digit, and one special symbol (+, -, *, ^, etc.)"
    )
    private String newPassword;
    @NotBlank(message = "Password is mandatory")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[+\\-*/^@#$%!&]).{8,}$",
            message = "Password must contain at least 8 characters, including one uppercase letter, one digit, and one special symbol (+, -, *, ^, etc.)"
    )
    private String confirmPassword;

    public ChangePasswordDto() {
    }

    public ChangePasswordDto(String oldPassword, String newPassword, String confirmPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
}
