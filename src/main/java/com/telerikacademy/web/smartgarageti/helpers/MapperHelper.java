package com.telerikacademy.web.smartgarageti.helpers;

import com.telerikacademy.web.smartgarageti.models.Model;
import com.telerikacademy.web.smartgarageti.models.Vehicle;
import com.telerikacademy.web.smartgarageti.models.dto.ModelDto;
import com.telerikacademy.web.smartgarageti.models.dto.VehicleDto;
import com.telerikacademy.web.smartgarageti.services.contracts.BrandService;
import com.telerikacademy.web.smartgarageti.services.contracts.ModelService;
import com.telerikacademy.web.smartgarageti.services.contracts.YearService;
import org.springframework.stereotype.Component;

@Component
public class MapperHelper {
    private final BrandService brandService;
    private final ModelService modelService;
    private final YearService yearService;

    public MapperHelper(BrandService brandService, ModelService modelService, YearService yearService) {
        this.brandService = brandService;
        this.modelService = modelService;
        this.yearService = yearService;
    }

    public Model createModelFromModelDto(ModelDto modelDto) {
        Model model = new Model();
        model.setName(modelDto.getModelName());
        return model;
    }
}
