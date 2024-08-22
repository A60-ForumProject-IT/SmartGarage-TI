package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.ClientCar;

import java.util.List;

public interface ClientCarService {
    List<ClientCar> getAllClientCars();

    ClientCar getClientCarById(int id);

    ClientCar createClientCar(ClientCar clientCar);

    ClientCar findByVin(String vin);

    ClientCar findByLicensePlate(String licensePlate);
}
