package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o WHERE o.clientCar.id = :clientCarId AND o.status = 'NOT_STARTED'")
    Optional<Order> findActiveOrderByClientCarId(@Param("clientCarId") int clientCarId);

    List<Order> findAllByClientCarOwnerId(int userId);

    List<Order> findByClientCar_IdAndStatusNot(int clientCarId, String status);

}
