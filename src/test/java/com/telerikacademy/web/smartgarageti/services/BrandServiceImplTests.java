package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.Brand;
import com.telerikacademy.web.smartgarageti.repositories.contracts.BrandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrandServiceImplTests {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandServiceImpl brandService;

    @Test
    void findBrandByName_ShouldReturnBrand_WhenBrandExists() {
        String brandName = "Audi";
        Brand mockBrand = new Brand(brandName);

        when(brandRepository.findByName(brandName)).thenReturn(Optional.of(mockBrand));

        Brand result = brandService.findBrandByName(brandName);

        assertEquals(mockBrand, result);
        verify(brandRepository).findByName(brandName);
    }

    @Test
    void findBrandByName_ShouldThrowEntityNotFoundException_WhenBrandDoesNotExist() {
        String brandName = "Audi";

        when(brandRepository.findByName(brandName)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> brandService.findBrandByName(brandName));

        verify(brandRepository).findByName(brandName);
    }

    @Test
    void findAllBrands_ShouldReturnAllBrands() {
        List<Brand> mockBrands = List.of(new Brand("Audi"), new Brand("Volkswagen"), new Brand("Porsche"));

        when(brandRepository.findAll()).thenReturn(mockBrands);

        List<Brand> result = brandService.findAllBrands();

        assertEquals(mockBrands, result);
        verify(brandRepository).findAll();
    }

    @Test
    void findBrandById_ShouldReturnBrand_WhenBrandExists() {
        int brandId = 1;
        Brand mockBrand = new Brand("Audi");
        mockBrand.setId(brandId);

        when(brandRepository.findById(brandId)).thenReturn(Optional.of(mockBrand));

        Brand result = brandService.findBrandById(brandId);

        assertEquals(mockBrand, result);
        verify(brandRepository).findById(brandId);
    }

    @Test
    void findBrandById_ShouldThrowEntityNotFoundException_WhenBrandDoesNotExist() {
        int brandId = 1;

        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> brandService.findBrandById(brandId));

        verify(brandRepository).findById(brandId);
    }

    @Test
    void createBrand_ShouldSaveAndReturnNewBrand_WhenBrandDoesNotExist() {
        String brandName = "Audi";
        Brand mockBrand = new Brand(brandName);

        when(brandRepository.findByName(brandName)).thenReturn(Optional.empty());
        when(brandRepository.save(any(Brand.class))).thenReturn(mockBrand);

        Brand result = brandService.createBrand(brandName);

        assertEquals(mockBrand, result);
        verify(brandRepository).findByName(brandName);
        verify(brandRepository).save(any(Brand.class));
    }

    @Test
    void createBrand_ShouldThrowDuplicateEntityException_WhenBrandAlreadyExists() {
        String brandName = "Audi";
        Brand mockBrand = new Brand(brandName);

        when(brandRepository.findByName(brandName)).thenReturn(Optional.of(mockBrand));

        assertThrows(DuplicateEntityException.class, () -> brandService.createBrand(brandName));

        verify(brandRepository).findByName(brandName);
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void deleteBrand_ShouldDeleteBrand_WhenBrandExists() {
        int brandId = 1;
        Brand mockBrand = new Brand("Audi");
        mockBrand.setId(brandId);

        when(brandRepository.findById(brandId)).thenReturn(Optional.of(mockBrand));

        brandService.deleteBrand(brandId);

        verify(brandRepository).findById(brandId);
        verify(brandRepository).delete(mockBrand);
    }

    @Test
    void deleteBrand_ShouldThrowEntityNotFoundException_WhenBrandDoesNotExist() {
        int brandId = 1;

        when(brandRepository.findById(brandId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> brandService.deleteBrand(brandId));

        verify(brandRepository).findById(brandId);
        verify(brandRepository, never()).delete(any(Brand.class));
    }

    @Test
    void findOrCreateBrand_ShouldReturnExistingBrand_WhenBrandExists() {
        String brandName = "Audi";
        Brand mockBrand = new Brand(brandName);

        when(brandRepository.findByName(brandName)).thenReturn(Optional.of(mockBrand));

        Brand result = brandService.findOrCreateBrand(brandName);

        assertEquals(mockBrand, result);
        verify(brandRepository).findByName(brandName);
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    void findOrCreateBrand_ShouldSaveAndReturnNewBrand_WhenBrandDoesNotExist() {
        String brandName = "Audi";
        Brand mockBrand = new Brand(brandName);

        when(brandRepository.findByName(brandName)).thenReturn(Optional.empty());
        when(brandRepository.save(any(Brand.class))).thenReturn(mockBrand);

        Brand result = brandService.findOrCreateBrand(brandName);

        assertEquals(mockBrand, result);
        verify(brandRepository).findByName(brandName);
        verify(brandRepository).save(any(Brand.class));
    }
}
