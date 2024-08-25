package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.RepairService;
import com.telerikacademy.web.smartgarageti.repositories.contracts.RepairServiceRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.RepairServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepairServiceServiceImpl implements RepairServiceService {
    private final RepairServiceRepository serviceRepository;

    @Autowired
    public RepairServiceServiceImpl(RepairServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public RepairService findServiceByName(String serviceName) {
        return serviceRepository.findByName(serviceName)
                .orElseThrow(() -> new EntityNotFoundException("Service", "name", serviceName));
    }

    @Override
    public List<RepairService> filterServices(String name, Double price, Sort sort) {
        return serviceRepository.findAllByNameAndPrice(name, price, sort);
    }

    @Override
    public List<RepairService> findAllServices() {
        return serviceRepository.findAllByIsDeletedFalse();
    }

    @Override
    public RepairService findServiceById(int id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service", id));
    }

    @Override
    public RepairService createService(RepairService service) {
        serviceRepository.findByName(service.getName()).ifPresent(year -> {
            throw new DuplicateEntityException("Service", service.getName());
        });

        return serviceRepository.save(service);
    }

    @Override
    public void deleteService(int id) {
        RepairService service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service", id));

        service.setDeleted(true);
        serviceRepository.save(service);
    }
}
