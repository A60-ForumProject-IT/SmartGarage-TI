package com.telerikacademy.web.smartgarageti.models.dto;

import com.telerikacademy.web.smartgarageti.exceptions.constraints.ValidYear;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientCarDtoMvc {

    @NotEmpty
    @Pattern(regexp = "^[A-Z0-9]{17}$", message = "Invalid VIN format!")
    private String vin;

    @NotEmpty
    @Pattern(regexp = "^(A|B|CH|Y|TX|H|CC|PP|T|P|BT|EB|CT|X|K|CM|PB|OB|EH|PA|E|KH|PK|CA|C|CB|CO|BP|M|BH)\\d{4}[ABEKMHOPCTYX]{2}$",
            message = "Invalid format for license plate!")
    private String license_plate;

    private String owner;

    @NotEmpty
    @Size(min = 2, max = 50, message = "Brand name should be between 2 and 50 symbols.")
    @Pattern(regexp = "Audi|Porsche|Volkswagen", message = "Brand name must be Audi, Porsche, or Volkswagen.")
    private String brandName;

    @NotEmpty
    @Size(min = 2, max = 50, message = "Model name should be between 2 and 50 symbols.")
    private String modelName;

    @NotEmpty
    @Pattern(regexp = "^\\d\\.\\d\\s[A-Za-z0-9\\s]{1,21}$",
            message = "Engine type must be in the format 'X.X Y', where X.X is a number and Y is a string of up to 21 characters.")
    private String engineType;

    @ValidYear
    private int year;

}
