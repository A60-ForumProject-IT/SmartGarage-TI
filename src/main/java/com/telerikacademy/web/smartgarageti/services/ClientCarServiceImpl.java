package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.repositories.contracts.ClientCarRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.ClientCarService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<ClientCar> getAllClientCars() {
        return clientCarRepository.findAll();
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
    public List<ClientCar> filterAndSortClientCarsByOwner(String searchTerm, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.ASC, sortBy); // Default to ascending

        if ("desc".equalsIgnoreCase(sortDirection)) {
            sort = Sort.by(Sort.Direction.DESC, sortBy);
        }

        return clientCarRepository.findAllByOwnerUsernameContainingIgnoreCaseOrOwnerFirstNameContainingIgnoreCase(searchTerm, searchTerm, sort);
    }

    @Override
    public List<ClientCar> getClientCarsByClientId(int clientId) {
        return clientCarRepository.findAllByOwnerId(clientId);
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
}
