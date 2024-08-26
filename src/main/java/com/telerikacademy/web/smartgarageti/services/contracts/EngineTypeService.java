package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.EngineType;

import java.util.List;

public interface EngineTypeService {
    EngineType findEngineTypeByName(String engineType);

    List<EngineType> findAllEngineTypes();

    EngineType findEngineTypeById(int id);

    EngineType createEngineType(String engineType);

    void deleteEngineType(int id);

    EngineType findOrCreateEngineType(String engineType);
}
