package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.Brand;
import com.telerikacademy.web.smartgarageti.repositories.contracts.BrandRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public Brand findBrandByName(String name) {
        return brandRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Brand", "name", name));
    }

    public List<Brand> findAllBrands() {
        return brandRepository.findAll();
    }

    public Brand findBrandById(int id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand", id));
    }

    public Brand createBrand(String brandName) {
        brandRepository.findByName(brandName).ifPresent(year -> {
            throw new DuplicateEntityException("Brand", brandName);
        });

        Brand newBrand = new Brand(brandName);

        return brandRepository.save(newBrand);
    }

    public void deleteBrand(int id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Brand", id));

        brandRepository.delete(brand);
    }
}
