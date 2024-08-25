package com.telerikacademy.web.smartgarageti.controllers.rest;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.models.RepairService;
import com.telerikacademy.web.smartgarageti.models.dto.RepairServiceDto;
import com.telerikacademy.web.smartgarageti.services.contracts.RepairServiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class RepairServiceRestController {
    private final RepairServiceService repairServiceService;
    private final MapperHelper mapperHelper;

    @Autowired
    public RepairServiceRestController(RepairServiceService repairServiceService, MapperHelper mapperHelper) {
        this.repairServiceService = repairServiceService;
        this.mapperHelper = mapperHelper;
    }

    @GetMapping("/filter-sort")
    public List<RepairService> filterAndSortServices(@RequestParam(required = false) String name,
                                                     @RequestParam(required = false) Double price,
                                                     @RequestParam(defaultValue = "name") String sortBy,
                                                     @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            Sort sort = Sort.by(Sort.Direction.ASC, sortBy);

            if ("desc".equalsIgnoreCase(sortDirection)) {
                sort = Sort.by(Sort.Direction.DESC, sortBy);
            }

            List<RepairService> services = repairServiceService.filterServices(name, price, sort);

            if (services.isEmpty()) {
                throw new EntityNotFoundException();
            }

            return services;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No services found matching the criteria.");
        }
    }

    @PostMapping
    public RepairService createNewRepairService(@Valid @RequestBody RepairServiceDto repairServiceDto) {
        try {
            RepairService repairService = mapperHelper.createRepairServiceFromDto(repairServiceDto);
            return repairServiceService.createService(repairService);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{serviceId}")
    public RepairService updateService(@PathVariable int serviceId, @Valid @RequestBody RepairServiceDto serviceDto) {
        try {
            RepairService repairService = mapperHelper.updateServiceFromDto(serviceDto, serviceId);
            return repairServiceService.createService(repairService);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{serviceId}")
    public void deleteService(@PathVariable int serviceId) {
        try {
            repairServiceService.deleteService(serviceId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
