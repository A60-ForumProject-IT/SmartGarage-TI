package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.Vehicle;

public interface VehicleService {
    Vehicle createVehicle(String brandName, String modelName, int yearValue);
}
