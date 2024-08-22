package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.Year;
import com.telerikacademy.web.smartgarageti.repositories.contracts.YearRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.YearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YearServiceImpl implements YearService {
    private final YearRepository yearRepository;

    @Autowired
    public YearServiceImpl(YearRepository yearRepository) {
        this.yearRepository = yearRepository;
    }

    @Override
    public Year findByYear(int year) {
        return yearRepository.findByYear(year)
                .orElseThrow(() -> new EntityNotFoundException("Year"));
    }

    @Override
    public List<Year> findAllYears() {
        return yearRepository.findAll();
    }

    @Override
    public Year findYearById(int id) {
        return yearRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Year", id));
    }

    @Override
    public Year createYear(int yearValue) {
        yearRepository.findByYear(yearValue).ifPresent(year -> {
            throw new DuplicateEntityException("Year", yearValue);
        });

        Year newYear = new Year(yearValue);

        return yearRepository.save(newYear);
    }

    @Override
    public void deleteYear(int id) {
        Year year = yearRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Year", id));

        yearRepository.delete(year);
    }
}
