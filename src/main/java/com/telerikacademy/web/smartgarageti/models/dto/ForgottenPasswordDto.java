package com.telerikacademy.web.smartgarageti.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgottenPasswordDto {
    private String email;

    public ForgottenPasswordDto() {
    }

    public ForgottenPasswordDto(String email) {
        this.email = email;
    }
}
