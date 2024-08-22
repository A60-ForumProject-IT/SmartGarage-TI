package com.telerikacademy.web.smartgarageti.helpers;

import com.telerikacademy.web.smartgarageti.models.*;
import com.telerikacademy.web.smartgarageti.models.dto.*;
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

    public static User toUserEntity(UserCreationDto dto, String randomPassword, Role role) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPassword(randomPassword);
        user.setRole(role);
        return user;
    }

    public ClientCar createClientCarFromDto(ClientCarDto clientCarDto, User userToAddCar, Vehicle vehicleToBeAdded) {
        ClientCar clientCar = new ClientCar();
        clientCar.setVin(clientCarDto.getVin());
        clientCar.setLicensePlate(clientCarDto.getLicense_plate());
        clientCar.setOwner(userToAddCar);
        clientCar.setVehicle(vehicleToBeAdded);
        return clientCar;
    }
}
