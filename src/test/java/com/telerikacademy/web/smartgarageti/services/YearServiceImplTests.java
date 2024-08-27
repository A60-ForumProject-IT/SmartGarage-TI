package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.Year;
import com.telerikacademy.web.smartgarageti.repositories.contracts.YearRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class YearServiceImplTests {

    @Mock
    private YearRepository yearRepository;

    @InjectMocks
    private YearServiceImpl yearService;

    @Test
    void findByYear_ShouldReturnYear_WhenYearExists() {
        int yearValue = 2020;
        Year mockYear = new Year(yearValue);

        when(yearRepository.findByYear(yearValue)).thenReturn(Optional.of(mockYear));

        Year result = yearService.findByYear(yearValue);

        assertEquals(mockYear, result);
        verify(yearRepository).findByYear(yearValue);
    }

    @Test
    void findByYear_ShouldThrowEntityNotFoundException_WhenYearDoesNotExist() {
        int yearValue = 2020;

        when(yearRepository.findByYear(yearValue)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> yearService.findByYear(yearValue));

        verify(yearRepository).findByYear(yearValue);
    }

    @Test
    void findAllYears_ShouldReturnAllYears() {
        List<Year> mockYears = List.of(new Year(2020), new Year(2021), new Year(2022));

        when(yearRepository.findAll()).thenReturn(mockYears);

        List<Year> result = yearService.findAllYears();

        assertEquals(mockYears, result);
        verify(yearRepository).findAll();
    }

    @Test
    void findYearById_ShouldReturnYear_WhenYearExists() {
        int yearId = 1;
        Year mockYear = new Year(2020);
        mockYear.setId(yearId);

        when(yearRepository.findById(yearId)).thenReturn(Optional.of(mockYear));

        Year result = yearService.findYearById(yearId);

        assertEquals(mockYear, result);
        verify(yearRepository).findById(yearId);
    }

    @Test
    void findYearById_ShouldThrowEntityNotFoundException_WhenYearDoesNotExist() {
        int yearId = 1;

        when(yearRepository.findById(yearId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> yearService.findYearById(yearId));

        verify(yearRepository).findById(yearId);
    }

    @Test
    void createYear_ShouldSaveAndReturnNewYear_WhenYearDoesNotExist() {
        int yearValue = 2020;
        Year mockYear = new Year(yearValue);

        when(yearRepository.findByYear(yearValue)).thenReturn(Optional.empty());
        when(yearRepository.save(any(Year.class))).thenReturn(mockYear);

        Year result = yearService.createYear(yearValue);

        assertEquals(mockYear, result);
        verify(yearRepository).findByYear(yearValue);
        verify(yearRepository).save(any(Year.class));
    }

    @Test
    void createYear_ShouldThrowDuplicateEntityException_WhenYearAlreadyExists() {
        int yearValue = 2020;
        Year mockYear = new Year(yearValue);

        when(yearRepository.findByYear(yearValue)).thenReturn(Optional.of(mockYear));

        assertThrows(DuplicateEntityException.class, () -> yearService.createYear(yearValue));

        verify(yearRepository).findByYear(yearValue);
        verify(yearRepository, never()).save(any(Year.class));
    }

    @Test
    void deleteYear_ShouldDeleteYear_WhenYearExists() {
        int yearId = 1;
        Year mockYear = new Year(2020);
        mockYear.setId(yearId);

        when(yearRepository.findById(yearId)).thenReturn(Optional.of(mockYear));

        yearService.deleteYear(yearId);

        verify(yearRepository).findById(yearId);
        verify(yearRepository).delete(mockYear);
    }

    @Test
    void deleteYear_ShouldThrowEntityNotFoundException_WhenYearDoesNotExist() {
        int yearId = 1;

        when(yearRepository.findById(yearId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> yearService.deleteYear(yearId));

        verify(yearRepository).findById(yearId);
        verify(yearRepository, never()).delete(any(Year.class));
    }

    @Test
    void findOrCreateYear_ShouldReturnExistingYear_WhenYearExists() {
        int yearValue = 2020;
        Year mockYear = new Year(yearValue);

        when(yearRepository.findByYear(yearValue)).thenReturn(Optional.of(mockYear));

        Year result = yearService.findOrCreateYear(yearValue);

        assertEquals(mockYear, result);
        verify(yearRepository).findByYear(yearValue);
        verify(yearRepository, never()).save(any(Year.class));
    }

    @Test
    void findOrCreateYear_ShouldSaveAndReturnNewYear_WhenYearDoesNotExist() {
        int yearValue = 2020;
        Year mockYear = new Year(yearValue);

        when(yearRepository.findByYear(yearValue)).thenReturn(Optional.empty());
        when(yearRepository.save(any(Year.class))).thenReturn(mockYear);

        Year result = yearService.findOrCreateYear(yearValue);

        assertEquals(mockYear, result);
        verify(yearRepository).findByYear(yearValue);
        verify(yearRepository).save(any(Year.class));
    }
}
