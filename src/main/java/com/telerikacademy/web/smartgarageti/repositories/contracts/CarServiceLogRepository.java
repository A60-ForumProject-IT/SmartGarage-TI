package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.CarServiceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarServiceLogRepository extends JpaRepository<CarServiceLog, Integer> {
    List<CarServiceLog> findAllByClientCarOwnerId(int ownerId);

    List<CarServiceLog> findAllByClientCarId(int clientCarId);
}
