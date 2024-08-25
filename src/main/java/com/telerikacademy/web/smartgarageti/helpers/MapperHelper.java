package com.telerikacademy.web.smartgarageti.helpers;

import com.telerikacademy.web.smartgarageti.models.*;
import com.telerikacademy.web.smartgarageti.models.dto.*;
import com.telerikacademy.web.smartgarageti.repositories.contracts.UserRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MapperHelper {
    private final ClientCarService clientCarService;
    private final BrandService brandService;
    private final ModelService modelService;
    private final YearService yearService;
    private final EngineTypeService engineTypeService;
    private final VehicleService vehicleService;
    private final RepairServiceService repairService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public MapperHelper(ClientCarService clientCarService, BrandService brandService, ModelService modelService, YearService yearService, EngineTypeService engineTypeService, VehicleService vehicleService, RepairServiceService repairService, UserService userService, UserRepository userRepository) {
        this.clientCarService = clientCarService;
        this.brandService = brandService;
        this.modelService = modelService;
        this.yearService = yearService;
        this.engineTypeService = engineTypeService;
        this.vehicleService = vehicleService;
        this.repairService = repairService;
        this.userService = userService;
        this.userRepository = userRepository;
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

    public ClientCar createClientCarFromDto(ClientCarDto clientCarDto, User userToAddCar) {
        Vehicle vehicle = vehicleService.getVehicleByDetails(clientCarDto.getBrandName(),
                        clientCarDto.getModelName(),
                        clientCarDto.getYear(),
                        clientCarDto.getEngineType())
                .orElseGet(() -> createNewVehicle(clientCarDto));

        ClientCar clientCar = new ClientCar();
        clientCar.setVin(clientCarDto.getVin());
        clientCar.setLicensePlate(clientCarDto.getLicense_plate());
        clientCar.setOwner(userToAddCar);
        clientCar.setVehicle(vehicle);

        return clientCar;
    }

    public ClientCar updateClientCarFromDto(ClientCarUpdateDto clientCarUpdateDto, int id) {
        ClientCar clientCar = clientCarService.getClientCarById(id);
        clientCar.setLicensePlate(clientCarUpdateDto.getLicense_plate());
        clientCar.setVin(clientCarUpdateDto.getVin());
        return clientCar;
    }

    public RepairService updateServiceFromDto(RepairServiceDto repairServiceDto, int serviceId) {
        RepairService service = repairService.findServiceById(serviceId);
        service.setName(repairServiceDto.getName());
        service.setPrice(repairServiceDto.getPrice());
        return service;
    }

    public RepairService createRepairServiceFromDto(RepairServiceDto repairServiceDto) {
        RepairService repairService = new RepairService();
        repairService.setName(repairServiceDto.getName());
        repairService.setPrice(repairServiceDto.getPrice());
        return repairService;
    }

    public User editUserFromDto(UserEditInfoDto userEditInfoDto, int id) {
        User user = userRepository.getUserById(id);
        user.setFirstName(userEditInfoDto.getFirstName());
        user.setLastName(userEditInfoDto.getLastName());
        user.setEmail(userEditInfoDto.getEmail());
        user.setPhoneNumber(userEditInfoDto.getPhoneNumber());
        return user;
    }

    private Vehicle createNewVehicle(ClientCarDto clientCarDto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(brandService.findOrCreateBrand(clientCarDto.getBrandName()));
        vehicle.setModel(modelService.findOrCreateModel(clientCarDto.getModelName()));
        vehicle.setYear(yearService.findOrCreateYear(clientCarDto.getYear()));
        vehicle.setEngineType(engineTypeService.findOrCreateEngineType(clientCarDto.getEngineType()));

        return vehicleService.createVehicle(clientCarDto.getBrandName(), clientCarDto.getModelName(),
                clientCarDto.getYear(), clientCarDto.getEngineType());
    }
}
