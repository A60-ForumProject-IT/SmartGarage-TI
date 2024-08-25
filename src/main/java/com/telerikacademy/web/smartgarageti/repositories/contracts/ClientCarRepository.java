package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.ClientCar;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientCarRepository extends JpaRepository<ClientCar, Integer> {
    Optional<ClientCar> findByVin(String vin);

    Optional<ClientCar> findByLicensePlate(String licensePlate);

    List<ClientCar> findAllByOwnerUsernameContainingIgnoreCaseOrOwnerFirstNameContainingIgnoreCase(String username, String firstName, Sort sort);

    List<ClientCar> findAllByOwnerId(int userId);
}
