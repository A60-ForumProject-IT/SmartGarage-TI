package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o WHERE o.clientCar.id = :clientCarId AND o.status = 'NOT_STARTED'")
    Optional<Order> findActiveOrderByClientCarId(@Param("clientCarId") int clientCarId);

    List<Order> findAllByClientCarOwnerId(int userId);

    List<Order> findByClientCar_IdAndStatusNot(int clientCarId, String status);

    @Query("SELECT o FROM Order o WHERE " +
            "(:orderId IS NULL OR o.id = :orderId) AND " +
            "(:ownerUsername IS NULL OR LOWER(o.clientCar.owner.username) LIKE LOWER(CONCAT('%', :ownerUsername, '%'))) AND " +
            "(:licensePlate IS NULL OR LOWER(o.clientCar.licensePlate) LIKE LOWER(CONCAT('%', :licensePlate, '%'))) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:visitedBefore IS NULL OR o.orderDate <= :visitedBefore) AND " +
            "(:visitedAfter IS NULL OR o.orderDate >= :visitedAfter)")
    Page<Order> findAllByCriteria(
            @Param("orderId") Integer orderId,
            @Param("ownerUsername") String ownerUsername,
            @Param("licensePlate") String licensePlate,
            @Param("status") String status,
            @Param("visitedBefore") LocalDate visitedBefore,
            @Param("visitedAfter") LocalDate visitedAfter,
            Pageable pageable);
}
