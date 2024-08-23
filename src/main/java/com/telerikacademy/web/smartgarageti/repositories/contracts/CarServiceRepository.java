package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.CarService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarServiceRepository extends JpaRepository<CarService, Integer> {
    List<CarService> findAllByClientCarOwnerId(int ownerId);

    List<CarService> findAllByClientCarId(int clientCarId);
}
