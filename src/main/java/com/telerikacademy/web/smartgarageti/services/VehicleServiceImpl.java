package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.Brand;
import com.telerikacademy.web.smartgarageti.models.Model;
import com.telerikacademy.web.smartgarageti.models.Vehicle;
import com.telerikacademy.web.smartgarageti.models.Year;
import com.telerikacademy.web.smartgarageti.repositories.contracts.VehicleRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.BrandService;
import com.telerikacademy.web.smartgarageti.services.contracts.ModelService;
import com.telerikacademy.web.smartgarageti.services.contracts.VehicleService;
import com.telerikacademy.web.smartgarageti.services.contracts.YearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final BrandService brandService;
    private final ModelService modelService;
    private final YearService yearService;
    private final VehicleRepository vehicleRepository;

    @Autowired
    public VehicleServiceImpl(BrandService brandService, ModelService modelService,
                          YearService yearService, VehicleRepository vehicleRepository) {
        this.brandService = brandService;
        this.modelService = modelService;
        this.yearService = yearService;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public Vehicle getVehicleById(int id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", id));
    }

    @Override
    public void deleteVehicleById(int id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", id));

        vehicleRepository.delete(vehicle);
    }

    @Override
    public Vehicle createVehicle(String brandName, String modelName, int yearValue) {
        Brand brand = brandService.findOrCreateBrand(brandName);
        Model model = modelService.findOrCreateModel(modelName);
        Year year = yearService.findOrCreateYear(yearValue);

        Optional<Vehicle> existingVehicle = vehicleRepository.findByBrandAndModelAndYear(brand, model, year);
        if (existingVehicle.isPresent()) {
            throw new DuplicateEntityException("Vehicle", brandName, modelName, yearValue);
        } else {
            Vehicle newVehicle = new Vehicle(brand, model, year);
            return vehicleRepository.save(newVehicle);
        }
    }
}
