package com.telerikacademy.web.smartgarageti.models.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RepairServiceDto {

    @NotEmpty
    private String name;

    @NotEmpty
    @Positive(message = "Price must be a positive number!")
    private Double price;
}
