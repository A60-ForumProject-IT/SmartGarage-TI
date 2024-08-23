package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.models.CarServiceLog;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.models.RepairService;
import com.telerikacademy.web.smartgarageti.repositories.contracts.CarServiceLogRepository;
import com.telerikacademy.web.smartgarageti.repositories.contracts.ClientCarRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.CarServiceLogService;
import com.telerikacademy.web.smartgarageti.services.contracts.ClientCarService;
import com.telerikacademy.web.smartgarageti.services.contracts.RepairServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarServiceLogServiceImpl implements CarServiceLogService {
    private final RepairServiceService repairServiceService;
    private final ClientCarService clientCarService;
    private final CarServiceLogRepository carServiceRepository;
    private final ClientCarRepository clientCarRepository;

    @Autowired
    public CarServiceLogServiceImpl(RepairServiceService repairServiceService,
                                    ClientCarService clientCarService,
                                    CarServiceLogRepository carServiceRepository,
                                    ClientCarRepository clientCarRepository) {
        this.repairServiceService = repairServiceService;
        this.clientCarService = clientCarService;
        this.carServiceRepository = carServiceRepository;
        this.clientCarRepository = clientCarRepository;
    }

    @Override
    public void addServiceToClientCar(int clientCarId, int serviceId) {
        ClientCar clientCar = clientCarService.getClientCarById(clientCarId);
        List<CarServiceLog> clientCarServices = clientCar.getCarServices();
        RepairService repairService = repairServiceService.findServiceById(serviceId);

        CarServiceLog carService = new CarServiceLog(repairService, clientCar);

        clientCarServices.add(carService);

        clientCarRepository.save(clientCar);
        carServiceRepository.save(carService);
    }

    @Override
    public List<CarServiceLog> findAllCarServices() {
        return carServiceRepository.findAll();
    }

    @Override
    public List<CarServiceLog> findCarServicesByClientCarId(int clientCarId) {
        return carServiceRepository.findAllByClientCarId(clientCarId);
    }

    @Override
    public List<CarServiceLog> findAllServicesByOwnerId(int ownerId) {
        return carServiceRepository.findAllByClientCarOwnerId(ownerId);
    }
}
