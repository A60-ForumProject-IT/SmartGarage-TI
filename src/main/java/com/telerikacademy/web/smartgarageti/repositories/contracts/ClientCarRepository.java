package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.models.EngineType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientCarRepository extends JpaRepository<ClientCar, Integer> {
    Optional<ClientCar> findByVin(String vin);

    Optional<ClientCar> findByLicensePlate(String licensePlate);
}
