package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.Year;

import java.util.List;
import java.util.Optional;

public interface YearService {
    Year findByYear(int year);
}