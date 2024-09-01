package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.NoResultsFoundException;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.repositories.contracts.ClientCarRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.ClientCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientCarServiceImpl implements ClientCarService {
    private final ClientCarRepository clientCarRepository;

    @Autowired
    public ClientCarServiceImpl(ClientCarRepository clientCarRepository) {
        this.clientCarRepository = clientCarRepository;
    }

    @Override
    public Page<ClientCar> getAllClientCars(Pageable pageable) {
        return clientCarRepository.findAllByIsDeletedFalse(pageable);
    }

    @Override
    public Page<ClientCar> filterAndSortClientCarsByOwner(String searchTerm, Pageable pageable) {
        return clientCarRepository.findAllByOwnerAndSort(searchTerm, pageable);
    }

    @Override
    public ClientCar getClientCarById(int id) {
        return clientCarRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Client car", id));
    }

    @Override
    public ClientCar findByVin(String vin) {
        return clientCarRepository.findByVin(vin)
                .orElseThrow(() -> new EntityNotFoundException("Client car", "vin", vin));
    }

    @Override
    public ClientCar findByLicensePlate(String licensePlate) {
        return clientCarRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new EntityNotFoundException("Client car", "license plate", licensePlate));
    }

    @Override
    public List<ClientCar> filterAndSortClientCarsByOwner(User loggedInUser, String searchTerm, String sortBy, String sortDirection) {
        PermissionHelper.isEmployee(loggedInUser, "You are not employee and can't filter client cars!");
        Sort sort = Sort.by(Sort.Direction.ASC, sortBy);

        if ("desc".equalsIgnoreCase(sortDirection)) {
            sort = Sort.by(Sort.Direction.DESC, sortBy);
        }

        List<ClientCar> clientCars = clientCarRepository.
                findAllByOwnerUsernameContainingIgnoreCaseOrOwnerFirstNameContainingIgnoreCaseAndIsDeletedFalse(searchTerm, searchTerm, sort);

        if (clientCars.isEmpty()) {
            throw new NoResultsFoundException("There are no client cars with the given search term");
        }
        return clientCars;
    }

    @Override
    public List<ClientCar> getClientCarsByClientId(int clientId, User loggedInUser, User carOwner) {
        PermissionHelper.isEmployeeOrSameUser(loggedInUser, carOwner, "You need to be employee or same user to check this info!");
        List<ClientCar> clientCars = clientCarRepository.findAllByOwnerIdAndIsDeletedFalse(clientId);

        if (clientCars.isEmpty()) {
            throw new NoResultsFoundException("This user does not have any client cars");
        }
        return clientCars;
    }

    @Override
    public ClientCar createClientCar(ClientCar clientCar) {
        clientCarRepository.findByVin(clientCar.getVin()).ifPresent(year -> {
            throw new DuplicateEntityException("VIN", clientCar.getVin());
        });

        clientCarRepository.findByLicensePlate(clientCar.getLicensePlate()).ifPresent(year -> {
            throw new DuplicateEntityException("License plate", clientCar.getLicensePlate());
        });

        return clientCarRepository.save(clientCar);
    }

    @Override
    public ClientCar createClientCar(ClientCar clientCar, User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't create client cars");

        clientCarRepository.findByVin(clientCar.getVin()).ifPresent(year -> {
            throw new DuplicateEntityException("VIN", clientCar.getVin());
        });

        clientCarRepository.findByLicensePlate(clientCar.getLicensePlate()).ifPresent(year -> {
            throw new DuplicateEntityException("License plate", clientCar.getLicensePlate());
        });

        return clientCarRepository.save(clientCar);
    }

    @Override
    public void updateClientCar(ClientCar clientCar, User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't update client cars!");
        clientCarRepository.save(clientCar);
    }

    @Override
    public void deleteClientCar(int id, User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't delete client cars!");
        ClientCar clientCar = clientCarRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client car", id));
        if (clientCar.isDeleted()) {
            throw new IllegalArgumentException("Client car is already deleted.");
        }
        clientCar.setDeleted(true);
        clientCarRepository.save(clientCar);
    }
}
