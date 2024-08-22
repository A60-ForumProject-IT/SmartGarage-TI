package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findByBrandAndModelAndYearAndEngineType(Brand brand, Model model, Year year, EngineType engineType);

    List<Vehicle> findAllByIsDeletedFalse();
}
