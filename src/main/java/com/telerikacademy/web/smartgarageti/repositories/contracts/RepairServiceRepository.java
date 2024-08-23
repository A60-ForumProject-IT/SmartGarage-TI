package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.RepairService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepairServiceRepository extends JpaRepository<RepairService, Integer> {
    Optional<RepairService> findByName(String name);
}
