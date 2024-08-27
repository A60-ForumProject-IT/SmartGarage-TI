package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.Model;
import com.telerikacademy.web.smartgarageti.repositories.contracts.ModelRepository;
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
public class ModelServiceImplTests {
    @Mock
    private ModelRepository modelRepository;

    @InjectMocks
    private ModelServiceImpl modelService;

    @Test
    void findModelByName_ShouldReturnModel_WhenModelExists() {
        String modelName = "A4";
        Model mockModel = new Model(modelName);

        when(modelRepository.findByName(modelName)).thenReturn(Optional.of(mockModel));

        Model result = modelService.findModelByName(modelName);

        assertEquals(mockModel, result);
        verify(modelRepository).findByName(modelName);
    }

    @Test
    void findModelByName_ShouldThrowEntityNotFoundException_WhenModelDoesNotExist() {
        String modelName = "A4";

        when(modelRepository.findByName(modelName)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> modelService.findModelByName(modelName));

        verify(modelRepository).findByName(modelName);
    }

    @Test
    void findAllModels_ShouldReturnAllModels() {
        List<Model> mockModels = List.of(new Model("A4"), new Model("Golf"), new Model("911"));

        when(modelRepository.findAll()).thenReturn(mockModels);

        List<Model> result = modelService.findAllModels();

        assertEquals(mockModels, result);
        verify(modelRepository).findAll();
    }

    @Test
    void findModelById_ShouldReturnModel_WhenModelExists() {
        int modelId = 1;
        Model mockModel = new Model("A4");
        mockModel.setId(modelId);

        when(modelRepository.findById(modelId)).thenReturn(Optional.of(mockModel));

        Model result = modelService.findModelById(modelId);

        assertEquals(mockModel, result);
        verify(modelRepository).findById(modelId);
    }

    @Test
    void findModelById_ShouldThrowEntityNotFoundException_WhenModelDoesNotExist() {
        int modelId = 1;

        when(modelRepository.findById(modelId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> modelService.findModelById(modelId));

        verify(modelRepository).findById(modelId);
    }

    @Test
    void createModel_ShouldSaveAndReturnNewModel_WhenModelDoesNotExist() {
        String modelName = "A4";
        Model mockModel = new Model(modelName);

        when(modelRepository.findByName(modelName)).thenReturn(Optional.empty());
        when(modelRepository.save(any(Model.class))).thenReturn(mockModel);

        Model result = modelService.createModel(modelName);

        assertEquals(mockModel, result);
        verify(modelRepository).findByName(modelName);
        verify(modelRepository).save(any(Model.class));
    }

    @Test
    void createModel_ShouldThrowDuplicateEntityException_WhenModelAlreadyExists() {
        String modelName = "A4";
        Model mockModel = new Model(modelName);

        when(modelRepository.findByName(modelName)).thenReturn(Optional.of(mockModel));

        assertThrows(DuplicateEntityException.class, () -> modelService.createModel(modelName));

        verify(modelRepository).findByName(modelName);
        verify(modelRepository, never()).save(any(Model.class));
    }

    @Test
    void deleteModel_ShouldDeleteModel_WhenModelExists() {
        int modelId = 1;
        Model mockModel = new Model("A4");
        mockModel.setId(modelId);

        when(modelRepository.findById(modelId)).thenReturn(Optional.of(mockModel));

        modelService.deleteModel(modelId);

        verify(modelRepository).findById(modelId);
        verify(modelRepository).delete(mockModel);
    }

    @Test
    void deleteModel_ShouldThrowEntityNotFoundException_WhenModelDoesNotExist() {
        int modelId = 1;

        when(modelRepository.findById(modelId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> modelService.deleteModel(modelId));

        verify(modelRepository).findById(modelId);
        verify(modelRepository, never()).delete(any(Model.class));
    }

    @Test
    void findOrCreateModel_ShouldReturnExistingModel_WhenModelExists() {
        String modelName = "A4";
        Model mockModel = new Model(modelName);

        when(modelRepository.findByName(modelName)).thenReturn(Optional.of(mockModel));

        Model result = modelService.findOrCreateModel(modelName);

        assertEquals(mockModel, result);
        verify(modelRepository).findByName(modelName);
        verify(modelRepository, never()).save(any(Model.class));
    }

    @Test
    void findOrCreateModel_ShouldSaveAndReturnNewModel_WhenModelDoesNotExist() {
        String modelName = "A4";
        Model mockModel = new Model(modelName);

        when(modelRepository.findByName(modelName)).thenReturn(Optional.empty());
        when(modelRepository.save(any(Model.class))).thenReturn(mockModel);

        Model result = modelService.findOrCreateModel(modelName);

        assertEquals(mockModel, result);
        verify(modelRepository).findByName(modelName);
        verify(modelRepository).save(any(Model.class));
    }
}
