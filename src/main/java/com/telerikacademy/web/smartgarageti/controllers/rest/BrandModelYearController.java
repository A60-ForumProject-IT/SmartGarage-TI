package com.telerikacademy.web.smartgarageti.controllers.rest;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.models.Brand;
import com.telerikacademy.web.smartgarageti.models.Model;
import com.telerikacademy.web.smartgarageti.models.Year;
import com.telerikacademy.web.smartgarageti.models.dto.ModelDto;
import com.telerikacademy.web.smartgarageti.services.contracts.BrandService;
import com.telerikacademy.web.smartgarageti.services.contracts.ModelService;
import com.telerikacademy.web.smartgarageti.services.contracts.YearService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BrandModelYearController {
    private final BrandService brandService;
    private final ModelService modelService;
    private final YearService yearService;
    private final MapperHelper mapperHelper;

    @Autowired
    public BrandModelYearController(BrandService brandService, ModelService modelService, YearService yearService, MapperHelper mapperHelper) {
        this.brandService = brandService;
        this.modelService = modelService;
        this.yearService = yearService;
        this.mapperHelper = mapperHelper;
    }

    @GetMapping("/years")
    public List<Year> findAllYears() {
        return yearService.findAllYears();
    }

    @GetMapping("/brands")
    public List<Brand> findAllBrands() {
        return brandService.findAllBrands();
    }

    @GetMapping("/models")
    public List<Model> findAllModels() {
        return modelService.findAllModels();
    }

    @PostMapping("/models")
    public Model createModel(@Valid @RequestBody ModelDto modelDto) {
        try {
            Model newModel = mapperHelper.createModelFromModelDto(modelDto);
            return modelService.createModel(newModel.getName());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/models/{modelId}")
    public Model getModelById(@PathVariable int modelId) {
        try {
            return modelService.findModelById(modelId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
