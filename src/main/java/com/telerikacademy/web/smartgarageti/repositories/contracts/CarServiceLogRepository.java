package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.CarServiceLog;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarServiceLogRepository extends JpaRepository<CarServiceLog, Integer> {
    List<CarServiceLog> findAllByClientCarOwnerId(int ownerId);

    List<CarServiceLog> findAllByClientCarId(int clientCarId);

    List<CarServiceLog> findAllByClientCarIn(List<ClientCar> clientCars);

    List<CarServiceLog> findAllByClientCarIdAndOrderStatus(int clientCarId, String status);

    CarServiceLog findByOrderIdAndClientCarId(int orderId, int clientCarId);

    void deleteByOrderIdAndClientCarId(int orderId, int clientCarId);
}
