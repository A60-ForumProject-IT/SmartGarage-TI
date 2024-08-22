package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.Brand;
import com.telerikacademy.web.smartgarageti.models.EngineType;
import com.telerikacademy.web.smartgarageti.repositories.contracts.EngineTypeRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.EngineTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EngineTypeServiceImpl implements EngineTypeService {
    private final EngineTypeRepository engineTypeRepository;

    @Autowired
    public EngineTypeServiceImpl(EngineTypeRepository engineTypeRepository) {
        this.engineTypeRepository = engineTypeRepository;
    }

    @Override
    public EngineType findEngineTypeByName(String engineType) {
        return engineTypeRepository.findByName(engineType)
                .orElseThrow(() -> new EntityNotFoundException("Engine", "type", engineType));
    }

    @Override
    public List<EngineType> findAllEngineTypes() {
        return engineTypeRepository.findAll();
    }

    @Override
    public EngineType findEngineTypeById(int id) {
        return engineTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Engine type", id));
    }

    @Override
    public EngineType createEngineType(String engineType) {
        engineTypeRepository.findByName(engineType).ifPresent(year -> {
            throw new DuplicateEntityException("Engine type", engineType);
        });

        EngineType newEngineType = new EngineType(engineType);

        return engineTypeRepository.save(newEngineType);
    }

    @Override
    public void deleteEngineType(int id) {
        EngineType engineType = engineTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Engine type", id));

        engineTypeRepository.delete(engineType);
    }

    @Override
    public EngineType findOrCreateEngineType(String engineType) {
        return engineTypeRepository.findByName(engineType)
                .orElseGet(() -> engineTypeRepository.save(new EngineType(engineType)));
    }
}
