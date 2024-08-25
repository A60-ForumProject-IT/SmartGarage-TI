package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.RepairService;
import com.telerikacademy.web.smartgarageti.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepairServiceRepository extends JpaRepository<RepairService, Integer> {
    Optional<RepairService> findByName(String name);

    List<RepairService> findAllByIsDeletedFalse();
}
