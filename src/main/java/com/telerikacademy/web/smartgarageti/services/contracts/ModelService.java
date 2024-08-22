package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.Model;
import com.telerikacademy.web.smartgarageti.models.Year;

import java.util.List;

public interface ModelService {
    Model findModelByName(String name);

    List<Model> findAllModels();

    Model findModelById(int id);

    Model createModel(String modelName);

    void deleteModel(int id);
}
