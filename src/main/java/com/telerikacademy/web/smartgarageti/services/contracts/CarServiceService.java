package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.CarService;

import java.util.List;

public interface CarServiceService {
    void addServiceToClientCar(int clientCarId, int serviceId);

    List<CarService> findAllCarServices();

    List<CarService> findCarServicesByClientCarId(int clientId);

    List<CarService> findAllServicesByOwnerId(int clientCarId);
}
