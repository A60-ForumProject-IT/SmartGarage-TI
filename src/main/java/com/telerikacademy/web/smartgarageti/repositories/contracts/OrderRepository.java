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

    @Query("SELECT o FROM Order o WHERE " +
            "(:orderId IS NULL OR o.id = :orderId) AND " +
            "(:ownerUsername IS NULL OR o.clientCar.owner.username = :ownerUsername) AND " +
            "(:licensePlate IS NULL OR o.clientCar.licensePlate = :licensePlate) AND " +
            "(:status IS NULL OR o.status = :status)")
    List<Order> findAllOrdersByCriteria(
            @Param("orderId") Integer orderId,
            @Param("ownerUsername") String ownerUsername,
            @Param("licensePlate") String licensePlate,
            @Param("status") String status
    );
}
