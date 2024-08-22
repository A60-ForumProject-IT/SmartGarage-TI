package com.telerikacademy.web.smartgarageti.controllers.rest;

import com.telerikacademy.web.smartgarageti.exceptions.AuthenticationException;
import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.Vehicle;
import com.telerikacademy.web.smartgarageti.models.dto.ClientCarDto;
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
public class ClientCarRestController {
    private final ClientCarService clientCarService;
    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final VehicleService vehicleService;
    private final MapperHelper mapperHelper;

    @Autowired
    public ClientCarRestController(ClientCarService clientCarService, AuthenticationHelper authenticationHelper, UserService userService, VehicleService vehicleService, MapperHelper mapperHelper) {
        this.clientCarService = clientCarService;
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.vehicleService = vehicleService;
        this.mapperHelper = mapperHelper;
    }


    @GetMapping("/client-cars")
    public List<ClientCar> getAllClientCars() {
        return clientCarService.getAllClientCars();
    }

    @PostMapping("/users/{userId}/client-cars/vehicles/{vehicleId}")
    public ClientCar addClientCarToVehicle(@PathVariable int userId,
                                           @Valid @RequestBody ClientCarDto clientCarDto,
                                           @PathVariable int vehicleId, @RequestHeader HttpHeaders httpHeaders) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(httpHeaders);
            User userToAddCar = userService.getUserById(loggedInUser, userId);
            Vehicle vehicleToBeAdded = vehicleService.getVehicleById(vehicleId);
            ClientCar clientCar = mapperHelper.createClientCarFromDto(clientCarDto, userToAddCar, vehicleToBeAdded);
            return clientCarService.createClientCar(clientCar);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/client-cars/{clientCarId}")
    public void updateClientCar(@PathVariable int clientCarId, @Valid @RequestBody ClientCarDto clientCarDto) {
        try {
            ClientCar existingClientCar = mapperHelper.updateClientCarFromDto(clientCarDto, clientCarId);
            clientCarService.createClientCar(existingClientCar);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
