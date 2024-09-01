package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Намира потребител по потребителско име
    Optional<User> findByUsername(String username);

    // Намира потребител по имейл
    Optional<User> findByEmail(String email);

    // Намира всички потребители по филтрирани критерии и сортиране
    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN u.clientCars cc " +
            "LEFT JOIN cc.carServices cs " +
            "LEFT JOIN cc.vehicle v " +
            "LEFT JOIN v.brand b " +
            "WHERE (:username IS NULL OR u.username LIKE %:username%) " +
            "AND (:email IS NULL OR u.email LIKE %:email%) " +
            "AND (:phoneNumber IS NULL OR u.phoneNumber LIKE %:phoneNumber%) " +
            "AND (:vehicleBrand IS NULL OR :vehicleBrand = '' OR LOWER(b.name) LIKE LOWER(CONCAT('%', :vehicleBrand, '%'))) " + // добавяне на проверка за празен низ
            "AND (:visitDateFrom IS NULL OR cs.serviceDate >= :visitDateFrom) " +
            "AND (:visitDateTo IS NULL OR cs.serviceDate <= :visitDateTo)")
    Page<User> findAllFiltered(
            @Param("username") String username,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("vehicleBrand") String vehicleBrand,
            @Param("visitDateFrom") LocalDate visitDateFrom,
            @Param("visitDateTo") LocalDate visitDateTo,
            Pageable pageable
    );

    @Query("SELECT u.username FROM User u WHERE u.username LIKE %:term%")
    List<String> findUsernamesByTerm(@Param("term") String term);
}
