package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.Vehicle;

import java.util.List;

public interface VehicleService {
    Vehicle createVehicle(String brandName, String modelName, int yearValue, String engineType);

    List<Vehicle> getAllVehicles();

    Vehicle getVehicleById(int id);

    void deleteVehicleById(int id);
}
