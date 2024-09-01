package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.ClientCar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClientCarRepository extends JpaRepository<ClientCar, Integer> {
    Optional<ClientCar> findByVin(String vin);

    Optional<ClientCar> findByLicensePlate(String licensePlate);

    List<ClientCar> findAllByOwnerUsernameContainingIgnoreCaseOrOwnerFirstNameContainingIgnoreCase(String username, String firstName, Sort sort);

    List<ClientCar> findAllByOwnerId(int userId);

    @Query("SELECT c FROM ClientCar c JOIN c.owner o WHERE " +
            "LOWER(o.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(o.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<ClientCar> findAllByOwnerAndSort(
            @Param("searchTerm") String searchTerm,
            Pageable pageable);
}
