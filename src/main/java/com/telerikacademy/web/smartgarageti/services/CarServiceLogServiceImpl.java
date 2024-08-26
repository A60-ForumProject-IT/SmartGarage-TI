package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.models.CarServiceLog;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.models.Order;
import com.telerikacademy.web.smartgarageti.models.RepairService;
import com.telerikacademy.web.smartgarageti.repositories.contracts.CarServiceLogRepository;
import com.telerikacademy.web.smartgarageti.repositories.contracts.ClientCarRepository;
import com.telerikacademy.web.smartgarageti.repositories.contracts.OrderRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.CarServiceLogService;
import com.telerikacademy.web.smartgarageti.services.contracts.ClientCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarServiceLogServiceImpl implements CarServiceLogService {
    private final ClientCarService clientCarService;
    private final CarServiceLogRepository carServiceRepository;
    private final ClientCarRepository clientCarRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public CarServiceLogServiceImpl(ClientCarService clientCarService, CarServiceLogRepository carServiceRepository,
                                    ClientCarRepository clientCarRepository, OrderRepository orderRepository) {
        this.clientCarService = clientCarService;
        this.carServiceRepository = carServiceRepository;
        this.clientCarRepository = clientCarRepository;
        this.orderRepository = orderRepository;
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

    @Override
    public CarServiceLog addServiceToOrder(int clientCarId, RepairService repairService) {
        Order activeOrder = orderRepository.findActiveOrderByClientCarId(clientCarId)
                .orElseGet(() -> createNewOrder(clientCarId));

        ClientCar clientCar = clientCarService.getClientCarById(clientCarId);
        List<CarServiceLog> clientCarServices = clientCar.getCarServices();

        CarServiceLog carServiceLog = new CarServiceLog(repairService, clientCar, activeOrder);

        clientCarServices.add(carServiceLog);

        clientCarRepository.save(clientCar);
        return carServiceRepository.save(carServiceLog);
    }

    @Override
    public List<CarServiceLog> getServiceHistoryForClientCars(List<ClientCar> clientCars) {
        return carServiceRepository.findAllByClientCarIn(clientCars);
    }

    private Order createNewOrder(int clientCarId) {
        Order newOrder = new Order();
        newOrder.setClientCar(clientCarService.getClientCarById(clientCarId));
        newOrder.setStatus("NOT_STARTED");

        return orderRepository.save(newOrder);
    }
}
