package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findByBrandAndModelAndYearAndEngineType(Brand brand, Model model, Year year, EngineType engineType);

    List<Vehicle> findAllByIsDeletedFalse();

    Optional<Vehicle> findByBrand_NameAndModel_NameAndYear_YearAndEngineType_Name(
            String brandName,
            String modelName,
            int year,
            String engineTypeName
    );

    Page<Vehicle> findAllByIsDeletedFalse(Pageable pageable);

}
