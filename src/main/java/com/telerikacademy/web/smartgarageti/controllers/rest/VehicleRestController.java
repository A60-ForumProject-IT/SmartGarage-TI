package com.telerikacademy.web.smartgarageti.controllers.rest;

import com.telerikacademy.web.smartgarageti.exceptions.AuthenticationException;
import com.telerikacademy.web.smartgarageti.exceptions.DeletedVehicleException;
import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.Vehicle;
import com.telerikacademy.web.smartgarageti.models.dto.ClientCarDto;
import com.telerikacademy.web.smartgarageti.models.dto.VehicleDto;
import com.telerikacademy.web.smartgarageti.services.contracts.ClientCarService;
import com.telerikacademy.web.smartgarageti.services.contracts.UserService;
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

    @Autowired
    public VehicleRestController(VehicleService vehicleService, AuthenticationHelper authenticationHelper, UserService userService, MapperHelper mapperHelper, ClientCarService clientCarService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping("/vehicles")
    public List<Vehicle> findAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/vehicles/{vehicleId}")
    public Vehicle getVehicleById(@PathVariable int vehicleId) {
        try {
            return vehicleService.getVehicleById(vehicleId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/vehicles")
    public Vehicle createVehicle(@Valid @RequestBody VehicleDto vehicleDto) {
        try {
            return vehicleService.createVehicle(vehicleDto.getBrandName(),
                    vehicleDto.getModelName(),
                    vehicleDto.getYear(),
                    vehicleDto.getEngineType());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (DeletedVehicleException e) {
            throw new ResponseStatusException(HttpStatus.LOCKED, e.getMessage());
        }
    }

    @DeleteMapping("/vehicles/{vehicleId}")
    public void deleteVehicleById(@PathVariable int vehicleId) {
        try {
            vehicleService.deleteVehicleById(vehicleId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}