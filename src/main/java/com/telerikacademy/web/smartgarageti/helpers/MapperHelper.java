package com.telerikacademy.web.smartgarageti.helpers;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.*;
import com.telerikacademy.web.smartgarageti.models.dto.*;
import com.telerikacademy.web.smartgarageti.repositories.contracts.UserRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MapperHelper {
    private final ClientCarService clientCarService;
    private final BrandService brandService;
    private final ModelService modelService;
    private final YearService yearService;
    private final EngineTypeService engineTypeService;
    private final VehicleService vehicleService;
    private final RepairServiceService repairService;
    private final UserRepository userRepository;

    @Autowired
    public MapperHelper(ClientCarService clientCarService, BrandService brandService,
                        ModelService modelService, YearService yearService,
                        EngineTypeService engineTypeService, VehicleService vehicleService,
                        RepairServiceService repairService, UserRepository userRepository) {
        this.clientCarService = clientCarService;
        this.brandService = brandService;
        this.modelService = modelService;
        this.yearService = yearService;
        this.engineTypeService = engineTypeService;
        this.vehicleService = vehicleService;
        this.repairService = repairService;
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

    public ClientCar createClientCarFromDto(ClientCarDto clientCarDto, User userToAddCar, User loggedInUser) {
        Vehicle vehicle = vehicleService.getVehicleByDetails(clientCarDto.getBrandName(),
                        clientCarDto.getModelName(),
                        clientCarDto.getYear(),
                        clientCarDto.getEngineType())
                .orElseGet(() -> createNewVehicle(clientCarDto, loggedInUser));

        ClientCar clientCar = new ClientCar();
        clientCar.setVin(clientCarDto.getVin());
        clientCar.setLicensePlate(clientCarDto.getLicense_plate());
        clientCar.setOwner(userToAddCar);
        clientCar.setVehicle(vehicle);

        return clientCar;
    }

    public ClientCar updateClientCarFromDto(ClientCarUpdateDto clientCarUpdateDto, int id) {
        ClientCar existingClientCar = clientCarService.getClientCarById(id);

        try {
            if (!existingClientCar.getVin().equals(clientCarUpdateDto.getVin())) {
                Optional<ClientCar> existingVin = Optional.ofNullable(clientCarService.findByVin(clientCarUpdateDto.getVin()));
                if (existingVin.isPresent()) {
                    throw new DuplicateEntityException("VIN already exists for another car");
                }
            }
        } catch (EntityNotFoundException e) {
            existingClientCar.setVin(clientCarUpdateDto.getVin());
        }

        try {
            if (!existingClientCar.getLicensePlate().equals(clientCarUpdateDto.getLicense_plate())) {
                Optional<ClientCar> existingLicensePlate = Optional.ofNullable(clientCarService.findByLicensePlate(clientCarUpdateDto.getLicense_plate()));
                if (existingLicensePlate.isPresent()) {
                    throw new DuplicateEntityException("License plate already exists for another car");
                }
            }
        } catch (EntityNotFoundException e) {
            existingClientCar.setLicensePlate(clientCarUpdateDto.getLicense_plate());
        }

        return existingClientCar;
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
        User user = userRepository.findById(id)
                .orElseThrow(()->
                        new EntityNotFoundException("User", id));
        user.setFirstName(userEditInfoDto.getFirstName());
        user.setLastName(userEditInfoDto.getLastName());
        user.setEmail(userEditInfoDto.getEmail());
        user.setPhoneNumber(userEditInfoDto.getPhoneNumber());
        return user;
    }

    public ClientCar createClientCarFromDto(ClientCarDtoMvc dto, User owner, Brand brand) {
        Vehicle vehicle = vehicleService.getVehicleByDetails(brand.getName(),
                        dto.getModelName(),
                        dto.getYear(),
                        dto.getEngineType())
                .orElseGet(() -> createNewVehicle(dto, owner, brand));

        ClientCar clientCar = new ClientCar();
        clientCar.setVin(dto.getVin());
        clientCar.setLicensePlate(dto.getLicense_plate());
        clientCar.setVehicle(vehicle);
        clientCar.setOwner(owner);
        return clientCar;
    }

    private Vehicle createNewVehicle(ClientCarDto clientCarDto, User user) {
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(brandService.findOrCreateBrand(clientCarDto.getBrandName()));
        vehicle.setModel(modelService.findOrCreateModel(clientCarDto.getModelName()));
        vehicle.setYear(yearService.findOrCreateYear(clientCarDto.getYear()));
        vehicle.setEngineType(engineTypeService.findOrCreateEngineType(clientCarDto.getEngineType()));

        return vehicleService.createVehicle(clientCarDto.getBrandName(), clientCarDto.getModelName(),
                clientCarDto.getYear(), clientCarDto.getEngineType(), user);
    }

    private Vehicle createNewVehicle(ClientCarDtoMvc clientCarDtoMvc, User user, Brand brand) {
        Vehicle vehicle = new Vehicle();
        vehicle.setBrand(brand);
        vehicle.setModel(modelService.findOrCreateModel(clientCarDtoMvc.getModelName()));
        vehicle.setYear(yearService.findOrCreateYear(clientCarDtoMvc.getYear()));
        vehicle.setEngineType(engineTypeService.findOrCreateEngineType(clientCarDtoMvc.getEngineType()));

        return vehicleService.createVehicle(brand.getName(), clientCarDtoMvc.getModelName(),
                clientCarDtoMvc.getYear(), clientCarDtoMvc.getEngineType(), user);
    }
}
