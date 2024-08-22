package com.telerikacademy.web.smartgarageti.repositories.contracts;

import com.telerikacademy.web.smartgarageti.models.Year;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface YearRepository extends JpaRepository<Year, Integer> {
    Optional<Year> findByYear(int year);
}
