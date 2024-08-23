package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.models.CarService;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.models.RepairService;
import com.telerikacademy.web.smartgarageti.repositories.contracts.CarServiceRepository;
import com.telerikacademy.web.smartgarageti.repositories.contracts.ClientCarRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.CarServiceService;
import com.telerikacademy.web.smartgarageti.services.contracts.ClientCarService;
import com.telerikacademy.web.smartgarageti.services.contracts.RepairServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarServiceServiceImpl implements CarServiceService {
    private final RepairServiceService repairServiceService;
    private final ClientCarService clientCarService;
    private final CarServiceRepository carServiceRepository;
    private final ClientCarRepository clientCarRepository;

    @Autowired
    public CarServiceServiceImpl(RepairServiceService repairServiceService,
                                 ClientCarService clientCarService,
                                 CarServiceRepository carServiceRepository,
                                 ClientCarRepository clientCarRepository) {
        this.repairServiceService = repairServiceService;
        this.clientCarService = clientCarService;
        this.carServiceRepository = carServiceRepository;
        this.clientCarRepository = clientCarRepository;
    }

    @Override
    public void addServiceToClientCar(int clientCarId, int serviceId) {
        ClientCar clientCar = clientCarService.getClientCarById(clientCarId);
        List<CarService> clientCarServices = clientCar.getCarServices();
        RepairService repairService = repairServiceService.findServiceById(serviceId);

        CarService carService = new CarService(repairService, clientCar);

        clientCarServices.add(carService);

        clientCarRepository.save(clientCar);
        carServiceRepository.save(carService);
    }

    @Override
    public List<CarService> findAllCarServices() {
        return carServiceRepository.findAll();
    }

    @Override
    public List<CarService> findCarServicesByClientCarId(int clientCarId) {
        return carServiceRepository.findAllByClientCarId(clientCarId);
    }

    @Override
    public List<CarService> findAllServicesByOwnerId(int ownerId) {
        return carServiceRepository.findAllByClientCarOwnerId(ownerId);
    }
}
