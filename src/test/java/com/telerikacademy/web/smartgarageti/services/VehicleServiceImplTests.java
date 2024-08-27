package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DeletedVehicleException;
import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.NoResultsFoundException;
import com.telerikacademy.web.smartgarageti.models.*;
import com.telerikacademy.web.smartgarageti.repositories.contracts.VehicleRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.BrandService;
import com.telerikacademy.web.smartgarageti.services.contracts.EngineTypeService;
import com.telerikacademy.web.smartgarageti.services.contracts.ModelService;
import com.telerikacademy.web.smartgarageti.services.contracts.YearService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.telerikacademy.web.smartgarageti.helpers.TestHelpers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceImplTests {
    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private BrandService brandService;

    @Mock
    private ModelService modelService;

    @Mock
    private YearService yearService;

    @Mock
    private EngineTypeService engineTypeService;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    @Test
    void getAllVehicles_ShouldPass_WhenThereAreVehicles() {
        List<Vehicle> mockVehicles = List.of(new Vehicle(), new Vehicle());

        when(vehicleRepository.findAllByIsDeletedFalse()).thenReturn(mockVehicles);

        List<Vehicle> result = vehicleService.getAllVehicles();

        assertEquals(mockVehicles, result);
        verify(vehicleRepository).findAllByIsDeletedFalse();
    }

    @Test
    void getAllVehicles_ShouldThrowNoResultsFoundException_WhenNoVehiclesExist() {
        when(vehicleRepository.findAllByIsDeletedFalse()).thenReturn(List.of());

        assertThrows(NoResultsFoundException.class, () -> vehicleService.getAllVehicles());
        verify(vehicleRepository).findAllByIsDeletedFalse();
    }

    @Test
    void getVehicleById_ShouldReturnVehicle_WhenVehicleExists() {
        int vehicleId = 1;
        Vehicle mockVehicle = new Vehicle();
        mockVehicle.setId(vehicleId);

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(mockVehicle));

        Vehicle result = vehicleService.getVehicleById(vehicleId);

        assertEquals(mockVehicle, result);
        verify(vehicleRepository).findById(vehicleId);
    }

    @Test
    void getVehicleById_ShouldThrowEntityNotFoundException_WhenVehicleDoesNotExist() {
        int vehicleId = 1;

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vehicleService.getVehicleById(vehicleId));
        verify(vehicleRepository).findById(vehicleId);
    }

    @Test
    void deleteVehicleById_ShouldMarkVehicleAsDeleted_WhenVehicleExists() {
        int vehicleId = 1;
        Vehicle mockVehicle = new Vehicle();
        mockVehicle.setId(vehicleId);
        mockVehicle.setDeleted(false);

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(mockVehicle));

        vehicleService.deleteVehicleById(vehicleId, createMockUserEmployee());

        assertTrue(mockVehicle.isDeleted());
        verify(vehicleRepository).save(mockVehicle);
    }

    @Test
    void getVehicleByDetails_ShouldReturnVehicle_WhenVehicleExists() {
        String brandName = "Audi";
        String modelName = "A4";
        int year = 2020;
        String engineType = "1.9 TDI";

        Vehicle mockVehicle = new Vehicle();
        when(vehicleRepository.findByBrand_NameAndModel_NameAndYear_YearAndEngineType_Name(
                brandName, modelName, year, engineType)).thenReturn(Optional.of(mockVehicle));

        Optional<Vehicle> result = vehicleService.getVehicleByDetails(brandName, modelName, year, engineType);

        assertEquals(Optional.of(mockVehicle), result);
        verify(vehicleRepository).findByBrand_NameAndModel_NameAndYear_YearAndEngineType_Name(
                brandName, modelName, year, engineType);
    }

    @Test
    void updateVehicle_ShouldSaveVehicle_WhenVehicleIsNotDeleted() {
        Vehicle mockVehicle = new Vehicle();
        mockVehicle.setDeleted(false);

        vehicleService.updateVehicle(mockVehicle, createMockUserEmployee());

        verify(vehicleRepository).save(mockVehicle);
    }

    @Test
    void updateVehicle_ShouldThrowDeletedVehicleException_WhenVehicleIsDeleted() {
        Vehicle mockVehicle = new Vehicle();
        mockVehicle.setDeleted(true);

        assertThrows(DeletedVehicleException.class, () ->
                vehicleService.updateVehicle(mockVehicle, createMockUserEmployee()));
        verify(vehicleRepository, never()).save(mockVehicle);
    }

    @Test
    void createVehicle_ShouldSaveNewVehicle_WhenVehicleDoesNotExist() {
        String brandName = "Audi";
        String modelName = "Q5";
        int yearValue = 2020;
        String engineType = "3.0 TDI";

        Vehicle mockVehicle = new Vehicle();
        mockVehicle.setDeleted(false);

        Brand mockBrand = new Brand(brandName);
        Model mockModel = new Model(modelName);
        Year mockYear = new Year(yearValue);
        EngineType mockEngineType = new EngineType(engineType);

        when(vehicleRepository.findByBrandAndModelAndYearAndEngineType(mockBrand, mockModel, mockYear, mockEngineType))
                .thenReturn(Optional.empty());

        when(brandService.findOrCreateBrand(brandName)).thenReturn(mockBrand);
        when(modelService.findOrCreateModel(modelName)).thenReturn(mockModel);
        when(yearService.findOrCreateYear(yearValue)).thenReturn(mockYear);
        when(engineTypeService.findOrCreateEngineType(engineType)).thenReturn(mockEngineType);

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(mockVehicle);

        Vehicle result = vehicleService.createVehicle(brandName, modelName, yearValue, engineType, createMockUserEmployee());

        assertEquals(mockVehicle, result);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void createVehicle_ShouldThrowDuplicateEntityException_WhenVehicleAlreadyExistsAndNotDeleted() {
        String brandName = "Toyota";
        String modelName = "Camry";
        int yearValue = 2020;
        String engineType = "Hybrid";

        Brand mockBrand = new Brand(brandName);
        Model mockModel = new Model(modelName);
        Year mockYear = new Year(yearValue);
        EngineType mockEngineType = new EngineType(engineType);

        Vehicle mockVehicle = new Vehicle();
        mockVehicle.setDeleted(false);

        when(brandService.findOrCreateBrand(eq(brandName))).thenReturn(mockBrand);
        when(modelService.findOrCreateModel(eq(modelName))).thenReturn(mockModel);
        when(yearService.findOrCreateYear(eq(yearValue))).thenReturn(mockYear);
        when(engineTypeService.findOrCreateEngineType(eq(engineType))).thenReturn(mockEngineType);

        when(vehicleRepository.findByBrandAndModelAndYearAndEngineType(eq(mockBrand), eq(mockModel), eq(mockYear), eq(mockEngineType)))
                .thenReturn(Optional.of(mockVehicle));

        assertThrows(DuplicateEntityException.class, () ->
                vehicleService.createVehicle(brandName, modelName, yearValue, engineType, createMockUserEmployee()));

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void createVehicle_ShouldThrowDeletedAgainVehicleException_WhenVehicleIsDeleted() {
        String brandName = "Toyota";
        String modelName = "Camry";
        int yearValue = 2020;
        String engineType = "Hybrid";

        Brand mockBrand = new Brand(brandName);
        Model mockModel = new Model(modelName);
        Year mockYear = new Year(yearValue);
        EngineType mockEngineType = new EngineType(engineType);

        Vehicle mockVehicle = new Vehicle();
        mockVehicle.setDeleted(true);

        when(brandService.findOrCreateBrand(eq(brandName))).thenReturn(mockBrand);
        when(modelService.findOrCreateModel(eq(modelName))).thenReturn(mockModel);
        when(yearService.findOrCreateYear(eq(yearValue))).thenReturn(mockYear);
        when(engineTypeService.findOrCreateEngineType(eq(engineType))).thenReturn(mockEngineType);

        when(vehicleRepository.findByBrandAndModelAndYearAndEngineType(eq(mockBrand), eq(mockModel), eq(mockYear), eq(mockEngineType)))
                .thenReturn(Optional.of(mockVehicle));

        assertThrows(DeletedVehicleException.class, () ->
                vehicleService.createVehicle(brandName, modelName, yearValue, engineType, createMockUserEmployee()));

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }
}
