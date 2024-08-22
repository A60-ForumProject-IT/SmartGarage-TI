package com.telerikacademy.web.smartgarageti.models.dto;


import com.telerikacademy.web.smartgarageti.exceptions.constraints.ValidYear;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDto {
    @NotEmpty
    @Size(min = 2, max = 50, message = "Brand name should be between 2 and 50 symbols.")
    private String brandName;

    @NotEmpty
    @Size(min = 2, max = 50, message = "Model name should be between 2 and 50 symbols.")
    private String modelName;

    @ValidYear
    private int year;
}
