package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.BaseService;
import com.telerikacademy.web.smartgarageti.models.Model;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BaseServiceRepository extends JpaRepository<BaseService, Integer> {
    Optional<BaseService> findByName(String name);

}
