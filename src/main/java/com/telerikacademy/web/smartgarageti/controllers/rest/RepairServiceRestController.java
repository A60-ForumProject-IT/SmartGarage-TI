package com.telerikacademy.web.smartgarageti.controllers.rest;

import com.telerikacademy.web.smartgarageti.exceptions.*;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.models.RepairService;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.RepairServiceDto;
import com.telerikacademy.web.smartgarageti.services.contracts.RepairServiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class RepairServiceRestController {
    private final RepairServiceService repairServiceService;
    private final MapperHelper mapperHelper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public RepairServiceRestController(RepairServiceService repairServiceService, MapperHelper mapperHelper, AuthenticationHelper authenticationHelper) {
        this.repairServiceService = repairServiceService;
        this.mapperHelper = mapperHelper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/filter-sort")
    public List<RepairService> filterAndSortServices(@RequestParam(required = false) String name,
                                                     @RequestParam(required = false) Double price,
                                                     @RequestParam(defaultValue = "name") String sortBy,
                                                     @RequestParam(defaultValue = "asc") String sortDirection,
                                                     @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            Sort sort = Sort.by(Sort.Direction.ASC, sortBy);

            if ("desc".equalsIgnoreCase(sortDirection)) {
                sort = Sort.by(Sort.Direction.DESC, sortBy);
            }

            return repairServiceService.filterServices(name, price, sort, loggedInUser);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (NoResultsFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping
    public RepairService createNewRepairService(@Valid @RequestBody RepairServiceDto repairServiceDto,
                                                @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            RepairService repairService = mapperHelper.createRepairServiceFromDto(repairServiceDto);
            return repairServiceService.createService(repairService, loggedInUser);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/{serviceId}")
    public RepairService updateService(@PathVariable int serviceId, @Valid @RequestBody RepairServiceDto serviceDto,
                                       @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            RepairService repairService = mapperHelper.updateServiceFromDto(serviceDto, serviceId);
            return repairServiceService.createService(repairService, loggedInUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @DeleteMapping("/{serviceId}")
    public void deleteService(@PathVariable int serviceId, @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            repairServiceService.deleteService(serviceId, loggedInUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}
