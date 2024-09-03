package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.RepairService;
import com.telerikacademy.web.smartgarageti.models.Vehicle;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RepairServiceRepository extends JpaRepository<RepairService, Integer> {
    Optional<RepairService> findByName(String name);

    List<RepairService> findAllByIsDeletedFalse();

    @Query("SELECT r FROM RepairService r WHERE (:name IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:price IS NULL OR r.price <= :price)")
    List<RepairService> findAllByNameAndPrice(@Param("name") String name,
                                              @Param("price") Double price, Sort sort);

    List<RepairService> findAllByBaseServiceId(Integer baseServiceId);

    List<RepairService> findAllByBaseService_IdAndIsDeletedFalse(int baseServiceId);

    List<RepairService> findByNameContainingIgnoreCase(String term);
}
