package com.telerikacademy.web.smartgarageti.models.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientCarUpdateDto {
    @NotEmpty
    @Pattern(regexp = "^[A-Z0-9]{17}$", message = "Invalid VIN format!")
    private String vin;

    @NotEmpty
    @Pattern(regexp = "^(A|B|CH|Y|TX|H|CC|PP|T|P|BT|EB|CT|X|K|CM|PB|OB|EH|PA|E|KH|PK|CA|C|CB|CO|BP|M|BH)\\d{4}[ABEKMHOPCTYX]{2}$",
            message = "Invalid format for license plate!")
    private String license_plate;
}
