package com.telerikacademy.web.smartgarageti.controllers.rest;

import com.telerikacademy.web.smartgarageti.exceptions.AuthenticationException;
import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.models.*;
import com.telerikacademy.web.smartgarageti.models.dto.ClientCarDto;
import com.telerikacademy.web.smartgarageti.services.contracts.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@Api(value = "ClientCar Management System", description = "Operations pertaining to client cars in ClientCar Management System")

public class ClientCarRestController {
    private final ClientCarService clientCarService;
    private final AuthenticationHelper authenticationHelper;
    private final UserService userService;
    private final VehicleService vehicleService;
    private final MapperHelper mapperHelper;
    private final CarServiceLogService carServiceService;
    private final RepairServiceService repairServiceService;

    @Autowired
    public ClientCarRestController(ClientCarService clientCarService,
                                   AuthenticationHelper authenticationHelper,
                                   UserService userService,
                                   VehicleService vehicleService,
                                   MapperHelper mapperHelper,
                                   CarServiceLogService carServiceService, RepairServiceService repairServiceService) {
        this.clientCarService = clientCarService;
        this.authenticationHelper = authenticationHelper;
        this.userService = userService;
        this.vehicleService = vehicleService;
        this.mapperHelper = mapperHelper;
        this.carServiceService = carServiceService;
        this.repairServiceService = repairServiceService;
    }

    @Operation(
            summary = "Filter and sort client cars by owner",
            description = "This filtration works with both username OR first name at once. Don't change 'owner'."
    )
    @GetMapping("/client-cars/filter-sort")
    public List<ClientCar> filterAndSortClientCarsByOwner(@RequestParam(required = false) String searchTerm,
                                                          @RequestParam(required = false, defaultValue = "owner") String sortBy,
                                                          @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        return clientCarService.filterAndSortClientCarsByOwner(searchTerm, sortBy, sortDirection);
    }

    @Operation(
            summary = "This method retrieves all client cars in the app."
    )
    @GetMapping("/client-cars")
    public List<ClientCar> getAllClientCars() {
        return clientCarService.getAllClientCars();
    }

    @Operation(
            summary = "Add a client car (VIN and license plate) to a vehicle that we've already got in our application."
    )
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

    @Operation(
            summary = "Update client car information.",
            description = "Works with ClientCarDto expecting VIN and license plate."
    )
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

    @Operation(
            summary = "Add a service to a client car."
    )
    @PostMapping("/client-cars/{clientCarId}/services/{serviceId}")
    public ResponseEntity<Void> addServiceToClientCar(@PathVariable int clientCarId, @PathVariable int serviceId) {
        try {
            RepairService repairService = repairServiceService.findServiceById(serviceId);
            carServiceService.addServiceToOrder(clientCarId, repairService);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(
            summary = "Finds all the services we've got in our application until this moment."
    )
    @GetMapping("/client-cars/services")
    public List<CarServiceLog> getAllCarServices() {
        return carServiceService.findAllCarServices();
    }

    @Operation(
            summary = "Finds all services to a concrete client car."
    )
    @GetMapping("/client-cars/{clientCarId}/services")
    public List<CarServiceLog> getClientCarServicesByClientCarId(@PathVariable int clientCarId) {
        return carServiceService.findCarServicesByClientCarId(clientCarId);
    }

    @Operation(
            summary = "Finds all services that have been done in the past for this user's cars."
    )
    @GetMapping("/users/{userId}/service-history")
    public ResponseEntity<List<CarServiceLog>> getServiceHistory(@PathVariable int userId, @RequestHeader HttpHeaders httpHeaders) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(httpHeaders);
            User user = userService.getUserById(loggedInUser, userId);

            List<ClientCar> clientCars = clientCarService.getClientCarsByClientId(userId);
            List<CarServiceLog> serviceLogs = carServiceService.getServiceHistoryForClientCars(clientCars);
            return ResponseEntity.ok(serviceLogs);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
