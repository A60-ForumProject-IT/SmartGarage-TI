package com.telerikacademy.web.smartgarageti.controllers.rest;

import com.telerikacademy.web.smartgarageti.models.Year;
import com.telerikacademy.web.smartgarageti.services.contracts.BrandService;
import com.telerikacademy.web.smartgarageti.services.contracts.ModelService;
import com.telerikacademy.web.smartgarageti.services.contracts.YearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BrandModelYearController {
    private final BrandService brandService;
    private final ModelService modelService;
    private final YearService yearService;

    @Autowired
    public BrandModelYearController(BrandService brandService, ModelService modelService, YearService yearService) {
        this.brandService = brandService;
        this.modelService = modelService;
        this.yearService = yearService;
    }

    @GetMapping("/years")
    public List<Year> findAllYears() {
        return yearService.findAllYears();
    }
}
