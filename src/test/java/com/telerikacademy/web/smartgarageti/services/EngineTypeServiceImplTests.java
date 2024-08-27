package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.EngineType;
import com.telerikacademy.web.smartgarageti.repositories.contracts.EngineTypeRepository;
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
public class EngineTypeServiceImplTests {
    @Mock
    private EngineTypeRepository engineTypeRepository;

    @InjectMocks
    private EngineTypeServiceImpl engineTypeService;

    @Test
    void findEngineTypeByName_ShouldReturnEngineType_WhenEngineTypeExists() {
        String engineTypeName = "Hybrid";
        EngineType mockEngineType = new EngineType(engineTypeName);

        when(engineTypeRepository.findByName(engineTypeName)).thenReturn(Optional.of(mockEngineType));

        EngineType result = engineTypeService.findEngineTypeByName(engineTypeName);

        assertEquals(mockEngineType, result);
        verify(engineTypeRepository).findByName(engineTypeName);
    }

    @Test
    void findEngineTypeByName_ShouldThrowEntityNotFoundException_WhenEngineTypeDoesNotExist() {
        String engineTypeName = "Hybrid";

        when(engineTypeRepository.findByName(engineTypeName)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> engineTypeService.findEngineTypeByName(engineTypeName));

        verify(engineTypeRepository).findByName(engineTypeName);
    }

    @Test
    void findAllEngineTypes_ShouldReturnAllEngineTypes() {
        List<EngineType> mockEngineTypes = List.of(new EngineType("Petrol"), new EngineType("Diesel"), new EngineType("Electric"));

        when(engineTypeRepository.findAll()).thenReturn(mockEngineTypes);

        List<EngineType> result = engineTypeService.findAllEngineTypes();

        assertEquals(mockEngineTypes, result);
        verify(engineTypeRepository).findAll();
    }

    @Test
    void findEngineTypeById_ShouldReturnEngineType_WhenEngineTypeExists() {
        int engineTypeId = 1;
        EngineType mockEngineType = new EngineType("Hybrid");
        mockEngineType.setId(engineTypeId);

        when(engineTypeRepository.findById(engineTypeId)).thenReturn(Optional.of(mockEngineType));

        EngineType result = engineTypeService.findEngineTypeById(engineTypeId);

        assertEquals(mockEngineType, result);
        verify(engineTypeRepository).findById(engineTypeId);
    }

    @Test
    void findEngineTypeById_ShouldThrowEntityNotFoundException_WhenEngineTypeDoesNotExist() {
        int engineTypeId = 1;

        when(engineTypeRepository.findById(engineTypeId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> engineTypeService.findEngineTypeById(engineTypeId));

        verify(engineTypeRepository).findById(engineTypeId);
    }

    @Test
    void createEngineType_ShouldSaveAndReturnNewEngineType_WhenEngineTypeDoesNotExist() {
        String engineTypeName = "Hybrid";
        EngineType mockEngineType = new EngineType(engineTypeName);

        when(engineTypeRepository.findByName(engineTypeName)).thenReturn(Optional.empty());
        when(engineTypeRepository.save(any(EngineType.class))).thenReturn(mockEngineType);

        EngineType result = engineTypeService.createEngineType(engineTypeName);

        assertEquals(mockEngineType, result);
        verify(engineTypeRepository).findByName(engineTypeName);
        verify(engineTypeRepository).save(any(EngineType.class));
    }

    @Test
    void createEngineType_ShouldThrowDuplicateEntityException_WhenEngineTypeAlreadyExists() {
        String engineTypeName = "Hybrid";
        EngineType mockEngineType = new EngineType(engineTypeName);

        when(engineTypeRepository.findByName(engineTypeName)).thenReturn(Optional.of(mockEngineType));

        assertThrows(DuplicateEntityException.class, () -> engineTypeService.createEngineType(engineTypeName));

        verify(engineTypeRepository).findByName(engineTypeName);
        verify(engineTypeRepository, never()).save(any(EngineType.class));
    }

    @Test
    void deleteEngineType_ShouldDeleteEngineType_WhenEngineTypeExists() {
        int engineTypeId = 1;
        EngineType mockEngineType = new EngineType("Hybrid");
        mockEngineType.setId(engineTypeId);

        when(engineTypeRepository.findById(engineTypeId)).thenReturn(Optional.of(mockEngineType));

        engineTypeService.deleteEngineType(engineTypeId);

        verify(engineTypeRepository).findById(engineTypeId);
        verify(engineTypeRepository).delete(mockEngineType);
    }

    @Test
    void deleteEngineType_ShouldThrowEntityNotFoundException_WhenEngineTypeDoesNotExist() {
        int engineTypeId = 1;

        when(engineTypeRepository.findById(engineTypeId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> engineTypeService.deleteEngineType(engineTypeId));

        verify(engineTypeRepository).findById(engineTypeId);
        verify(engineTypeRepository, never()).delete(any(EngineType.class));
    }

    @Test
    void findOrCreateEngineType_ShouldReturnExistingEngineType_WhenEngineTypeExists() {
        String engineTypeName = "Hybrid";
        EngineType mockEngineType = new EngineType(engineTypeName);

        when(engineTypeRepository.findByName(engineTypeName)).thenReturn(Optional.of(mockEngineType));

        EngineType result = engineTypeService.findOrCreateEngineType(engineTypeName);

        assertEquals(mockEngineType, result);
        verify(engineTypeRepository).findByName(engineTypeName);
        verify(engineTypeRepository, never()).save(any(EngineType.class));
    }

    @Test
    void findOrCreateEngineType_ShouldSaveAndReturnNewEngineType_WhenEngineTypeDoesNotExist() {
        String engineTypeName = "Hybrid";
        EngineType mockEngineType = new EngineType(engineTypeName);

        when(engineTypeRepository.findByName(engineTypeName)).thenReturn(Optional.empty());
        when(engineTypeRepository.save(any(EngineType.class))).thenReturn(mockEngineType);

        EngineType result = engineTypeService.findOrCreateEngineType(engineTypeName);

        assertEquals(mockEngineType, result);
        verify(engineTypeRepository).findByName(engineTypeName);
        verify(engineTypeRepository).save(any(EngineType.class));
    }
}
