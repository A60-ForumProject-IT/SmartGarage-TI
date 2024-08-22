package com.telerikacademy.web.smartgarageti.helpers;

import com.telerikacademy.web.smartgarageti.models.Model;
import com.telerikacademy.web.smartgarageti.models.Role;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.Vehicle;
import com.telerikacademy.web.smartgarageti.models.dto.ModelDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserCreationDto;
import com.telerikacademy.web.smartgarageti.models.dto.UserDto;
import com.telerikacademy.web.smartgarageti.models.dto.VehicleDto;
import com.telerikacademy.web.smartgarageti.services.contracts.BrandService;
import com.telerikacademy.web.smartgarageti.services.contracts.ModelService;
import com.telerikacademy.web.smartgarageti.services.contracts.YearService;
import org.springframework.stereotype.Component;

@Component
public class MapperHelper {
    private final BrandService brandService;
    private final ModelService modelService;
    private final YearService yearService;

    public MapperHelper(BrandService brandService, ModelService modelService, YearService yearService) {
        this.brandService = brandService;
        this.modelService = modelService;
        this.yearService = yearService;
    }

    public Model createModelFromModelDto(ModelDto modelDto) {
        Model model = new Model();
        model.setName(modelDto.getModelName());
        return model;
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }

    public static User toUserEntity(UserCreationDto dto, String hashedPassword, Role role) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(hashedPassword);
        user.setRole(role);
        return user;
    }
}
