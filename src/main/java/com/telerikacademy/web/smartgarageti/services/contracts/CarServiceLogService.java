package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.CarServiceLog;

import java.util.List;

public interface CarServiceLogService {
    void addServiceToClientCar(int clientCarId, int serviceId);

    List<CarServiceLog> findAllCarServices();

    List<CarServiceLog> findCarServicesByClientCarId(int clientId);

    List<CarServiceLog> findAllServicesByOwnerId(int clientCarId);
}
