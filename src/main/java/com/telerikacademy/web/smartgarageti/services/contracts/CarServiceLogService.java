package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.CarServiceLog;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.models.RepairService;
import com.telerikacademy.web.smartgarageti.models.User;

import java.util.List;

public interface CarServiceLogService {

    List<CarServiceLog> findAllCarsServiceLogs(User user);

    List<CarServiceLog> findCarServicesByClientCarId(int clientId, User user, User owner);

    List<CarServiceLog> findAllServicesByOwnerId(int clientCarId);

    CarServiceLog addServiceToOrder(int clientCarId, RepairService repairService, User user);

    List<CarServiceLog> getServiceHistoryForClientCars(List<ClientCar> clientCars, User loggedInUser, User carOwner);

    List<CarServiceLog> findNotStartedOrdersByClientCarId(int clientCarId, User user);

    void deleteServiceFromOrder(int clientServiceLogId, int clientCarId, User user);

    List<CarServiceLog> findOrderServicesByOrderId(int orderId, User user, User owner);

    List<CarServiceLog> findOrderServicesByOrderIdWithoutAuth(int orderId);
}
