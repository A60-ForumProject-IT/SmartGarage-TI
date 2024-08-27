package com.telerikacademy.web.smartgarageti.controllers.rest;

import com.telerikacademy.web.smartgarageti.exceptions.*;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.Vehicle;
import com.telerikacademy.web.smartgarageti.models.dto.VehicleDto;
import com.telerikacademy.web.smartgarageti.services.contracts.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VehicleRestController {
    private final VehicleService vehicleService;
    private final MapperHelper mapperHelper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public VehicleRestController(VehicleService vehicleService, MapperHelper mapperHelper, AuthenticationHelper authenticationHelper) {
        this.vehicleService = vehicleService;
        this.mapperHelper = mapperHelper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/vehicles")
    public List<Vehicle> findAllVehicles(@RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            return vehicleService.getAllVehicles();
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (NoResultsFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/vehicles/{vehicleId}")
    public Vehicle getVehicleById(@PathVariable int vehicleId, @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            return vehicleService.getVehicleById(vehicleId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/vehicles")
    public Vehicle createVehicle(@Valid @RequestBody VehicleDto vehicleDto, @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            return vehicleService.createVehicle(vehicleDto.getBrandName(),
                    vehicleDto.getModelName(),
                    vehicleDto.getYear(),
                    vehicleDto.getEngineType(), loggedInUser);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (DeletedVehicleException e) {
            throw new ResponseStatusException(HttpStatus.LOCKED, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/vehicles/{vehicleId}")
    public void updateVehicle(@PathVariable int vehicleId,
                              @Valid @RequestBody VehicleDto vehicleDto,
                              @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            Vehicle vehicle = mapperHelper.updateVehicleFromVehicleDto(vehicleDto, vehicleId);
            vehicleService.updateVehicle(vehicle, loggedInUser);
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DeletedVehicleException e) {
            throw new ResponseStatusException(HttpStatus.LOCKED, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @DeleteMapping("/vehicles/{vehicleId}")
    public void deleteVehicleById(@PathVariable int vehicleId, @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            vehicleService.deleteVehicleById(vehicleId, loggedInUser);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}
