package com.telerikacademy.web.smartgarageti.helpers;

import com.telerikacademy.web.smartgarageti.models.*;
import com.telerikacademy.web.smartgarageti.models.dto.*;
import com.telerikacademy.web.smartgarageti.services.contracts.*;
import org.springframework.stereotype.Component;

@Component
public class MapperHelper {
    private final ClientCarService clientCarService;
    private final BrandService brandService;
    private final ModelService modelService;
    private final YearService yearService;
    private final EngineTypeService engineTypeService;
    private final VehicleService vehicleService;

    public MapperHelper(ClientCarService clientCarService, BrandService brandService, ModelService modelService, YearService yearService, EngineTypeService engineTypeService, VehicleService vehicleService) {
        this.clientCarService = clientCarService;
        this.brandService = brandService;
        this.modelService = modelService;
        this.yearService = yearService;
        this.engineTypeService = engineTypeService;
        this.vehicleService = vehicleService;
    }

    public Model createModelFromModelDto(ModelDto modelDto) {
        Model model = new Model();
        model.setName(modelDto.getModelName());
        return model;
    }

    public Vehicle updateVehicleFromVehicleDto(VehicleDto vehicleDto, int id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        vehicle.setBrand(brandService.findOrCreateBrand(vehicleDto.getBrandName()));
        vehicle.setModel(modelService.findOrCreateModel(vehicleDto.getModelName()));
        vehicle.setYear(yearService.findOrCreateYear(vehicleDto.getYear()));
        vehicle.setEngineType(engineTypeService.findOrCreateEngineType(vehicleDto.getEngineType()));
        return vehicle;
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

    public ClientCar updateClientCarFromDto(ClientCarDto clientCarDto, int id) {
        ClientCar clientCar = clientCarService.getClientCarById(id);
        clientCar.setLicensePlate(clientCarDto.getLicense_plate());
        clientCar.setVin(clientCarDto.getVin());
        return clientCar;
    }
}
