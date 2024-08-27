package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.NoResultsFoundException;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.models.RepairService;
import com.telerikacademy.web.smartgarageti.models.User;
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
    public List<RepairService> filterServices(String name, Double price, Sort sort, User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't filter or sort services!");

        List<RepairService> repairServices = serviceRepository.findAllByNameAndPrice(name, price, sort);

        if (repairServices.isEmpty()) {
            throw new NoResultsFoundException("No repair services found");
        }
        return repairServices;
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
    public RepairService createService(RepairService service, User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't create or update services!");

        serviceRepository.findByName(service.getName()).ifPresent(year -> {
            throw new DuplicateEntityException("Service", service.getName());
        });

        return serviceRepository.save(service);
    }

    @Override
    public void deleteService(int id, User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't delete services!");

        RepairService service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Service", id));

        service.setDeleted(true);
        serviceRepository.save(service);
    }
}
