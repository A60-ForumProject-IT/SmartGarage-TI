package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.Service;
import com.telerikacademy.web.smartgarageti.repositories.contracts.ServiceRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceServiceImpl implements ServiceService {
    private final ServiceRepository serviceRepository;

    @Autowired
    public ServiceServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public Service findServiceByName(String serviceName) {
        return serviceRepository.findByName(serviceName)
                .orElseThrow(() -> new EntityNotFoundException("Service", "name", serviceName));
    }

    @Override
    public List<Service> findAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    public Service findServiceById(int id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service", id));
    }

    @Override
    public Service createService(Service service) {
        serviceRepository.findByName(service.getName()).ifPresent(year -> {
            throw new DuplicateEntityException("Service", service.getName());
        });

        return serviceRepository.save(service);
    }

    @Override
    public void deleteService(int id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service", id));

        serviceRepository.delete(service);
    }
}
