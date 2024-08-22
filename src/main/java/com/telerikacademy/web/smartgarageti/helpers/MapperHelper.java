package com.telerikacademy.web.smartgarageti.helpers;

import com.telerikacademy.web.smartgarageti.models.Model;
import com.telerikacademy.web.smartgarageti.models.dto.ModelDto;
import org.springframework.stereotype.Component;

@Component
public class MapperHelper {


    public Model createModelFromModelDto(ModelDto modelDto) {
        Model model = new Model();
        model.setName(modelDto.getModelName());
        return model;
    }
}
