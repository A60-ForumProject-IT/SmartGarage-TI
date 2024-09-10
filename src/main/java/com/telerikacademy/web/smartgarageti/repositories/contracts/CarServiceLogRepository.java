package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.CarServiceLog;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CarServiceLogRepository extends JpaRepository<CarServiceLog, Integer> {
    List<CarServiceLog> findAllByClientCarOwnerId(int ownerId);

    List<CarServiceLog> findAllByClientCarId(int clientCarId);

    List<CarServiceLog> findAllByClientCarIn(List<ClientCar> clientCars);

    List<CarServiceLog> findAllByClientCarIdAndOrderStatus(int clientCarId, String status);

    CarServiceLog findByOrderIdAndClientCarId(int orderId, int clientCarId);

    void deleteByOrderIdAndClientCarId(int orderId, int clientCarId);

    Optional<CarServiceLog> findByIdAndClientCarId(int clientServiceLogId, int clientCarId);

    @Query("SELECT c FROM CarServiceLog c WHERE c.order.id = :orderId")
    List<CarServiceLog> findAllByOrderId(@Param("orderId") int orderId);
}
