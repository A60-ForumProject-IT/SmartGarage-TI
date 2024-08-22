package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.Brand;
import com.telerikacademy.web.smartgarageti.models.Model;

import java.util.List;

public interface BrandService {
    Brand findBrandByName(String brandName);

    List<Brand> findAllBrands();

    Brand findBrandById(int id);

    Brand createBrand(String brandName);

    void deleteBrand(int id);

    Brand findOrCreateBrand(String brandName);
}
