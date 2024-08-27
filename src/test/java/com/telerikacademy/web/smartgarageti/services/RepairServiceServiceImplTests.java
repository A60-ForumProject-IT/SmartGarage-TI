package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.NoResultsFoundException;
import com.telerikacademy.web.smartgarageti.models.RepairService;
import com.telerikacademy.web.smartgarageti.repositories.contracts.RepairServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static com.telerikacademy.web.smartgarageti.helpers.TestHelpers.createMockUserEmployee;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RepairServiceServiceImplTests {

    @Mock
    private RepairServiceRepository repairServiceRepository;

    @InjectMocks
    private RepairServiceServiceImpl repairServiceService;

    @Test
    void findServiceByName_ShouldReturnService_WhenServiceExists() {
        String serviceName = "Oil Change";
        RepairService mockService = new RepairService(serviceName, 100.0);

        when(repairServiceRepository.findByName(serviceName)).thenReturn(Optional.of(mockService));

        RepairService result = repairServiceService.findServiceByName(serviceName);

        assertEquals(mockService, result);
        verify(repairServiceRepository).findByName(serviceName);
    }

    @Test
    void findServiceByName_ShouldThrowEntityNotFoundException_WhenServiceDoesNotExist() {
        String serviceName = "Oil Change";

        when(repairServiceRepository.findByName(serviceName)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> repairServiceService.findServiceByName(serviceName));

        verify(repairServiceRepository).findByName(serviceName);
    }

    @Test
    void filterServices_ShouldReturnFilteredServices_WhenServicesMatchCriteria() {
        String serviceName = "Oil";
        Double price = 100.0;
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        List<RepairService> mockServices = List.of(
                new RepairService("Oil Change", 100.0),
                new RepairService("Oil Filter", 50.0)
        );

        when(repairServiceRepository.findAllByNameAndPrice(serviceName, price, sort)).thenReturn(mockServices);

        List<RepairService> result = repairServiceService.filterServices(serviceName, price, sort, createMockUserEmployee());

        assertEquals(mockServices, result);
        verify(repairServiceRepository).findAllByNameAndPrice(serviceName, price, sort);
    }

    @Test
    void filterServices_ShouldThrowNoResultsFoundException_WhenNoServicesMatchCriteria() {
        String serviceName = "Oil";
        Double price = 100.0;
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        when(repairServiceRepository.findAllByNameAndPrice(serviceName, price, sort)).thenReturn(List.of());

        assertThrows(NoResultsFoundException.class, () ->
                repairServiceService.filterServices(serviceName, price, sort, createMockUserEmployee()));

        verify(repairServiceRepository).findAllByNameAndPrice(serviceName, price, sort);
    }

    @Test
    void findAllServices_ShouldReturnAllServices_WhenServicesExist() {
        List<RepairService> mockServices = List.of(
                new RepairService("Oil Change", 100.0),
                new RepairService("Tire Change", 200.0)
        );

        when(repairServiceRepository.findAllByIsDeletedFalse()).thenReturn(mockServices);

        List<RepairService> result = repairServiceService.findAllServices();

        assertEquals(mockServices, result);
        verify(repairServiceRepository).findAllByIsDeletedFalse();
    }

    @Test
    void findServiceById_ShouldReturnService_WhenServiceExists() {
        int serviceId = 1;
        RepairService mockService = new RepairService("Oil Change", 100.0);
        mockService.setId(serviceId);

        when(repairServiceRepository.findById(serviceId)).thenReturn(Optional.of(mockService));

        RepairService result = repairServiceService.findServiceById(serviceId);

        assertEquals(mockService, result);
        verify(repairServiceRepository).findById(serviceId);
    }

    @Test
    void findServiceById_ShouldThrowEntityNotFoundException_WhenServiceDoesNotExist() {
        int serviceId = 1;

        when(repairServiceRepository.findById(serviceId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> repairServiceService.findServiceById(serviceId));

        verify(repairServiceRepository).findById(serviceId);
    }

    @Test
    void createService_ShouldSaveAndReturnNewService_WhenServiceDoesNotExist() {
        RepairService mockService = new RepairService("Oil Change", 100.0);

        when(repairServiceRepository.findByName(mockService.getName())).thenReturn(Optional.empty());
        when(repairServiceRepository.save(any(RepairService.class))).thenReturn(mockService);

        RepairService result = repairServiceService.createService(mockService, createMockUserEmployee());

        assertEquals(mockService, result);
        verify(repairServiceRepository).findByName(mockService.getName());
        verify(repairServiceRepository).save(any(RepairService.class));
    }

    @Test
    void createService_ShouldThrowDuplicateEntityException_WhenServiceAlreadyExists() {
        RepairService mockService = new RepairService("Oil Change", 100.0);

        when(repairServiceRepository.findByName(mockService.getName())).thenReturn(Optional.of(mockService));

        assertThrows(DuplicateEntityException.class, () -> repairServiceService.createService(mockService, createMockUserEmployee()));

        verify(repairServiceRepository).findByName(mockService.getName());
        verify(repairServiceRepository, never()).save(any(RepairService.class));
    }

    @Test
    void deleteService_ShouldMarkServiceAsDeleted_WhenServiceExists() {
        int serviceId = 1;
        RepairService mockService = new RepairService("Oil Change", 100.0);
        mockService.setId(serviceId);

        when(repairServiceRepository.findById(serviceId)).thenReturn(Optional.of(mockService));

        repairServiceService.deleteService(serviceId, createMockUserEmployee());

        assertTrue(mockService.isDeleted());
        verify(repairServiceRepository).findById(serviceId);
        verify(repairServiceRepository).save(mockService);
    }

    @Test
    void deleteService_ShouldThrowEntityNotFoundException_WhenServiceDoesNotExist() {
        int serviceId = 1;

        when(repairServiceRepository.findById(serviceId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> repairServiceService.deleteService(serviceId, createMockUserEmployee()));

        verify(repairServiceRepository).findById(serviceId);
        verify(repairServiceRepository, never()).save(any(RepairService.class));
    }
}
