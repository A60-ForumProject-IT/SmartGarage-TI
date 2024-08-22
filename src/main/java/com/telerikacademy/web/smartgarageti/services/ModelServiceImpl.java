package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.Brand;
import com.telerikacademy.web.smartgarageti.models.Model;
import com.telerikacademy.web.smartgarageti.repositories.contracts.ModelRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelServiceImpl implements ModelService {
    private final ModelRepository modelRepository;

    @Autowired
    public ModelServiceImpl(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    @Override
    public Model findModelByName(String name) {
        return modelRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Model", "name", name));
    }

    @Override
    public List<Model> findAllModels() {
        return modelRepository.findAll();
    }

    @Override
    public Model findModelById(int id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Model", id));
    }

    @Override
    public Model createModel(String modelName) {
        modelRepository.findByName(modelName).ifPresent(year -> {
            throw new DuplicateEntityException("Model", modelName);
        });

        Model newModel = new Model(modelName);

        return modelRepository.save(newModel);
    }

    @Override
    public void deleteModel(int id) {
        Model model = modelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Model", id));

        modelRepository.delete(model);
    }

    @Override
    public Model findOrCreateModel(String modelName) {
        return modelRepository.findByName(modelName)
                .orElseGet(() -> modelRepository.save(new Model(modelName)));
    }
}
