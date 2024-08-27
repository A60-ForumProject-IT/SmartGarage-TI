package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    Vehicle createVehicle(String brandName, String modelName, int yearValue, String engineType, User user);

    List<Vehicle> getAllVehicles();

    Vehicle getVehicleById(int id);

    void deleteVehicleById(int id, User user);

    void updateVehicle(Vehicle vehicle, User user);

    Optional<Vehicle> getVehicleByDetails(String brandName, String modelName, int year, String engineType);
}
