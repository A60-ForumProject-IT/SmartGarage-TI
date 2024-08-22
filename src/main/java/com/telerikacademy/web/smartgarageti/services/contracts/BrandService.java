package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.Brand;

public interface BrandService {
    Brand findBrandByName(String name);
}
