package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.EngineType;
import com.telerikacademy.web.smartgarageti.models.Model;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EngineTypeRepository extends JpaRepository<EngineType, Integer> {
    Optional<EngineType> findByName(String name);
}
