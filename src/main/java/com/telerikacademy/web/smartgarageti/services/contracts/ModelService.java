package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.Model;

import java.util.List;

public interface ModelService {
    Model findModelByName(String name);

    List<Model> findAllModels();

    Model findModelById(int id);

    Model createModel(String modelName);

    void deleteModel(int id);

    Model findOrCreateModel(String modelName);
}
