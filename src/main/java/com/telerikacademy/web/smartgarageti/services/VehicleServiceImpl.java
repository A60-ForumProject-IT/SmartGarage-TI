package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DeletedVehicleException;
import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.NoResultsFoundException;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.models.*;
import com.telerikacademy.web.smartgarageti.repositories.contracts.VehicleRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final BrandService brandService;
    private final ModelService modelService;
    private final YearService yearService;
    private final VehicleRepository vehicleRepository;
    private final EngineTypeService engineTypeService;

    @Autowired
    public VehicleServiceImpl(BrandService brandService, ModelService modelService,
                              YearService yearService, VehicleRepository vehicleRepository, EngineTypeService engineTypeService) {
        this.brandService = brandService;
        this.modelService = modelService;
        this.yearService = yearService;
        this.vehicleRepository = vehicleRepository;
        this.engineTypeService = engineTypeService;
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAllByIsDeletedFalse();

        if (vehicles.isEmpty()) {
            throw new NoResultsFoundException("No vehicles found");
        }
        return vehicles;
    }

    @Override
    public Page<Vehicle> getAllVehicles(Pageable pageable) {
        return vehicleRepository.findAllByIsDeletedFalse(pageable);
    }

    @Override
    public Vehicle getVehicleById(int id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", id));
    }

    @Override
    public void deleteVehicleById(int id, User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't delete vehicles!");
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", id));
        vehicle.setDeleted(true);
        vehicleRepository.save(vehicle);
    }

    @Override
    public void updateVehicle(Vehicle vehicle, User user) {
        PermissionHelper.isEmployee(user, "You are not an employee and can't update vehicles!");

        if(vehicle.isDeleted()) {
            throw new DeletedVehicleException("Cannot update a deleted vehicle.");
        }
        vehicleRepository.save(vehicle);
    }

    @Override
    public Optional<Vehicle> getVehicleByDetails(String brandName, String modelName, int year, String engineType) {
        return vehicleRepository.findByBrand_NameAndModel_NameAndYear_YearAndEngineType_Name(brandName, modelName, year, engineType);
    }


    @Override
    public Vehicle createVehicle(String brandName, String modelName, int yearValue, String engineType, User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't create vehicles!");
        Brand brand = brandService.findOrCreateBrand(brandName);
        Model model = modelService.findOrCreateModel(modelName);
        Year year = yearService.findOrCreateYear(yearValue);
        EngineType engine = engineTypeService.findOrCreateEngineType(engineType);

        Optional<Vehicle> existingVehicle = vehicleRepository.findByBrandAndModelAndYearAndEngineType(brand, model, year, engine);
        if (existingVehicle.isPresent() && existingVehicle.get().isDeleted()) {
            throw new DeletedVehicleException("We don't service this vehicle anymore!");
        }
        if (existingVehicle.isPresent()) {
            throw new DuplicateEntityException("Vehicle", brandName, modelName, yearValue, engineType);
        } else {
            Vehicle newVehicle = new Vehicle(brand, model, year, engine);
            return vehicleRepository.save(newVehicle);
        }
    }

    @Override
    public Page<Vehicle> searchVehicles(String brand, String model, String year, String engineType, Pageable pageable) {
        Integer yearInt = null;
        if (year != null && !year.isEmpty()) {
            try {
                yearInt = Integer.parseInt(year);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Year must be a valid integer.");
            }
        }

        if (brand == null || brand.isEmpty()) {
            brand = null;
        }

        return vehicleRepository.searchVehicles(brand, model, yearInt, engineType, pageable);
    }
}
