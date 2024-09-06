package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT v FROM Vehicle v " +
            "WHERE v.isDeleted = false " +
            "AND (:brand IS NULL OR v.brand.name = :brand) " +  // Ако марката е null, пропускаме филтъра за марка
            "AND (:modelName IS NULL OR LOWER(v.model.name) LIKE LOWER(CONCAT('%', :modelName, '%'))) " +
            "AND (:year IS NULL OR v.year.year = :year) " +
            "AND (:engineType IS NULL OR LOWER(v.engineType.name) LIKE LOWER(CONCAT('%', :engineType, '%'))) ")
    Page<Vehicle> searchVehicles(
            @Param("brand") String brand,
            @Param("modelName") String modelName,
            @Param("year") Integer year,
            @Param("engineType") String engineType,
            Pageable pageable);
}

