package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClientCarService {
    Page<ClientCar> getAllClientCars(Pageable pageable);

    ClientCar getClientCarById(int id);

    ClientCar createClientCar(ClientCar clientCar);

    ClientCar findByVin(String vin);

    ClientCar findByLicensePlate(String licensePlate);

    List<ClientCar> filterAndSortClientCarsByOwner(User user, String searchTerm, String sortBy, String sortDirection);

    List<ClientCar> getClientCarsByClientId(int clientId, User loggedInUser, User carOwner);

    void updateClientCar(ClientCar clientCar, User user);

    Page<ClientCar> filterAndSortClientCarsByOwner(String searchTerm, Pageable pageable);

    ClientCar createClientCar(ClientCar clientCar, User user);

    void deleteClientCar(int id, User user);
}
