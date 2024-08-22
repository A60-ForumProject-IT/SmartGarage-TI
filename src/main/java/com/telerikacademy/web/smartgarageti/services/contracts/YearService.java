package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.Year;

import java.util.List;
import java.util.Optional;

public interface YearService {
    Year findByYear(int year);

    List<Year> findAllYears();

    Year findYearById(int id);

    Year createYear(int yearValue);

    void deleteYear(int id);
}
