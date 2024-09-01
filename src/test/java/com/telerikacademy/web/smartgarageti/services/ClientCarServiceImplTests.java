package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.NoResultsFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.Vehicle;
import com.telerikacademy.web.smartgarageti.repositories.contracts.ClientCarRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.telerikacademy.web.smartgarageti.helpers.TestHelpers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientCarServiceImplTests {

    @Mock
    private ClientCarRepository clientCarRepository;

    @InjectMocks
    private ClientCarServiceImpl carService;


    @Test
    public void createClientCar_ShouldThrow_IfVinExists() {
        ClientCar carToBeCreated = createMockClientCar();

        when(clientCarRepository.findByVin(carToBeCreated.getVin()))
                .thenReturn(Optional.of(carToBeCreated));

        Assertions.assertThrows(DuplicateEntityException.class, () -> carService.createClientCar(carToBeCreated));
    }

    @Test
    public void createClientCar_ShouldThrow_IfLicensePlateExists() {
        ClientCar carToBeCreated = createMockClientCar();

        when(clientCarRepository.findByLicensePlate(carToBeCreated.getLicensePlate()))
                .thenReturn(Optional.of(carToBeCreated));

        Assertions.assertThrows(DuplicateEntityException.class, () -> carService.createClientCar(carToBeCreated));
    }

    @Test
    public void createClientCar_ShouldCreateClientCar_WhenEverythingIsValid() {
        ClientCar mockClientCar = createMockClientCar();

        when(clientCarRepository.findByVin(anyString())).thenReturn(Optional.empty());
        when(clientCarRepository.findByLicensePlate(anyString())).thenReturn(Optional.empty());
        when(clientCarRepository.save(mockClientCar)).thenReturn(mockClientCar);

        ClientCar result = carService.createClientCar(mockClientCar);

        assertEquals(mockClientCar, result);
        verify(clientCarRepository).save(mockClientCar);
    }

    @Test
    public void updateShouldThrow_IfUserIsNotEmployee() {
        ClientCar clientCar = createMockClientCar();
        User mockUser = createMockUser();

        assertThrows(UnauthorizedOperationException.class,
                () -> carService.updateClientCar(clientCar, mockUser));
    }

    @Test
    public void updateClientCar_ShouldUpdateClientCar_WhenUserIsValid() {
        ClientCar clientCar = createMockClientCar();
        User mockUser = createMockUserEmployee();
        clientCar.setOwner(mockUser);

        carService.updateClientCar(clientCar, mockUser);

        verify(clientCarRepository).save(clientCar);
    }

    @Test
    public void getClientCars_ShouldThrow_IfNoResultsFound() {
        ClientCar clientCar = createMockClientCar();
        User mockUser = createMockUser();
        User carOwner = createMockUser();
        clientCar.setOwner(carOwner);

        assertThrows(NoResultsFoundException.class,
                () -> carService.getClientCarsByClientId(carOwner.getId(), mockUser, carOwner));
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void getClientCars_ShouldThrow_IfCarsAreFound() {
        int clientId = 1;
        User mockLoggedInUser = createMockUserEmployee();
        User mockCarOwner = createMockUser();
        List<ClientCar> mockClientCars = List.of(createMockClientCar());

        when(clientCarRepository.findAllByOwnerIdAndIsDeletedFalse(clientId)).thenReturn(mockClientCars);

        List<ClientCar> result = carService.getClientCarsByClientId(clientId, mockLoggedInUser, mockCarOwner);

        assertEquals(mockClientCars, result);
        verify(clientCarRepository).findAllByOwnerIdAndIsDeletedFalse(clientId);
    }

    @Test
    void filterAndSortClientCarsByOwner_ShouldReturnSortedClientCars_WhenCarsExist() {
        User mockLoggedInUser = createMockUserEmployee();
        String searchTerm = "search";
        String sortBy = "username";
        String sortDirection = "asc";
        Sort sort = Sort.by(Sort.Direction.ASC, sortBy);

        List<ClientCar> mockClientCars = List.of(createMockClientCar());

        when(clientCarRepository.findAllByOwnerUsernameContainingIgnoreCaseOrOwnerFirstNameContainingIgnoreCaseAndIsDeletedFalse(
                searchTerm, searchTerm, sort)).thenReturn(mockClientCars);

        List<ClientCar> result = carService.filterAndSortClientCarsByOwner(mockLoggedInUser, searchTerm, sortBy, sortDirection);

        assertEquals(mockClientCars, result);
        verify(clientCarRepository).findAllByOwnerUsernameContainingIgnoreCaseOrOwnerFirstNameContainingIgnoreCaseAndIsDeletedFalse(
                searchTerm, searchTerm, sort);
    }

    @Test
    void filterAndSortClientCarsByOwner_ShouldThrowNoResultsFoundException_WhenNoCarsFound() {
        User mockLoggedInUser = createMockUserEmployee();
        String searchTerm = "search";
        String sortBy = "username";
        String sortDirection = "asc";
        Sort sort = Sort.by(Sort.Direction.ASC, sortBy);

        when(clientCarRepository.findAllByOwnerUsernameContainingIgnoreCaseOrOwnerFirstNameContainingIgnoreCaseAndIsDeletedFalse(
                searchTerm, searchTerm, sort)).thenReturn(Collections.emptyList());

        assertThrows(NoResultsFoundException.class, () ->
                carService.filterAndSortClientCarsByOwner(mockLoggedInUser, searchTerm, sortBy, sortDirection));
    }

    @Test
    void filterAndSortClientCarsByOwner_ShouldUseDescendingSort_WhenSortDirectionIsDesc() {
        User mockLoggedInUser = createMockUserEmployee();
        String searchTerm = "search";
        String sortBy = "username";
        String sortDirection = "desc";
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);

        List<ClientCar> mockClientCars = List.of(createMockClientCar());

        when(clientCarRepository.findAllByOwnerUsernameContainingIgnoreCaseOrOwnerFirstNameContainingIgnoreCaseAndIsDeletedFalse(
                searchTerm, searchTerm, sort)).thenReturn(mockClientCars);

        List<ClientCar> result = carService.filterAndSortClientCarsByOwner(mockLoggedInUser, searchTerm, sortBy, sortDirection);

        assertEquals(mockClientCars, result);
        verify(clientCarRepository).findAllByOwnerUsernameContainingIgnoreCaseOrOwnerFirstNameContainingIgnoreCaseAndIsDeletedFalse(
                searchTerm, searchTerm, sort);
    }

    @Test
    void findByLicensePlate_ShouldThrow_WhenLicensePlateDoesNotExists() {
        ClientCar mockClientCar = createMockClientCar();

        assertThrows(EntityNotFoundException.class, () -> carService.findByLicensePlate(mockClientCar.getLicensePlate()));
    }

    @Test
    void findByVin_ShouldThrow_WhenVinPlateDoesNotExists() {
        ClientCar mockClientCar = createMockClientCar();

        assertThrows(EntityNotFoundException.class, () -> carService.findByVin(mockClientCar.getVin()));
    }

    @Test
    void getClientCarById_ShouldThrowEntityNotFoundException_WhenCarDoesNotExist() {
        int carId = 1;
        when(clientCarRepository.findById(carId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> carService.getClientCarById(carId));
        verify(clientCarRepository).findById(carId);
    }

    @Test
    void getAllClientCars_Should_Pass() {
        List<ClientCar> clientCars = new ArrayList<>();
        Vehicle vehicle = createMockVehicle();
        clientCars.add(new ClientCar("VIN1", "Plate1", new User(), vehicle));

        Pageable pageable = PageRequest.of(0, 10); // Симулира се първа страница с 10 резултата
        Page<ClientCar> clientCarPage = new PageImpl<>(clientCars, pageable, clientCars.size());

        when(clientCarRepository.findAll(pageable)).thenReturn(clientCarPage);

        carService.getAllClientCars(pageable);

        verify(clientCarRepository).findAll(pageable);
    }
}
