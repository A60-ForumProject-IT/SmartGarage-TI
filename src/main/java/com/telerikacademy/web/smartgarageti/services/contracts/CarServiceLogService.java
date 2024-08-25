package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.CarServiceLog;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.models.RepairService;

import java.util.List;

public interface CarServiceLogService {

//    void addServiceToClientCar(int clientCarId, int serviceId);

    List<CarServiceLog> findAllCarServices();

    List<CarServiceLog> findCarServicesByClientCarId(int clientId);

    List<CarServiceLog> findAllServicesByOwnerId(int clientCarId);

    CarServiceLog addServiceToOrder(int clientCarId, RepairService repairService);

    List<CarServiceLog> getServiceHistoryForClientCars(List<ClientCar> clientCars);
}
