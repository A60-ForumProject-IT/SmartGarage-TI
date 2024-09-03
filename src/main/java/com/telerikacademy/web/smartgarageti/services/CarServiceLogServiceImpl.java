package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.NoResultsFoundException;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.models.*;
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
    public List<CarServiceLog> findAllCarsServiceLogs(User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't see all service logs!");
        List<CarServiceLog> carServiceLogs = carServiceRepository.findAll();
        if (carServiceLogs.isEmpty()) {
            throw new NoResultsFoundException("No service logs found!");
        }
        return carServiceLogs;
    }

    @Override
    public List<CarServiceLog> findCarServicesByClientCarId(int clientCarId, User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't see these details!");
        List<CarServiceLog> carServiceLogs = carServiceRepository.findAllByClientCarId(clientCarId);

        if (carServiceLogs.isEmpty()) {
            throw new NoResultsFoundException("No service logs found for this car!");
        }
        return carServiceLogs;
    }

    @Override
    public List<CarServiceLog> findAllServicesByOwnerId(int ownerId) {
        return carServiceRepository.findAllByClientCarOwnerId(ownerId);
    }

    @Override
    public CarServiceLog addServiceToOrder(int clientCarId, RepairService repairService, User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't add services to cars!");
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
    public List<CarServiceLog> getServiceHistoryForClientCars(List<ClientCar> clientCars, User loggedInUser, User carOwner) {
        PermissionHelper.isEmployeeOrSameUser(loggedInUser, carOwner, "You are not employee or this user to check this details!");
        List<CarServiceLog> carServiceLogs = carServiceRepository.findAllByClientCarIn(clientCars);

        if (carServiceLogs.isEmpty()) {
            throw new NoResultsFoundException("No service logs found for this user!");
        }
        return carServiceLogs;
    }

    @Override
    public List<CarServiceLog> findNotStartedOrdersByClientCarId(int clientCarId, User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't see these details!");

        return carServiceRepository.findAllByClientCarIdAndOrderStatus(clientCarId, "NOT_STARTED");
    }

    @Override
    public void deleteServiceFromOrder(int orderId, int clientCarId, User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't delete service from order!");
        CarServiceLog carServiceLog = carServiceRepository.findByOrderIdAndClientCarId(orderId, clientCarId);

        if (carServiceLog == null) {
            throw new EntityNotFoundException("CarServiceLog not found for this order and car.");
        }

        carServiceRepository.delete(carServiceLog);
    }

    private Order createNewOrder(int clientCarId) {
        Order newOrder = new Order();
        newOrder.setClientCar(clientCarService.getClientCarById(clientCarId));
        newOrder.setStatus("NOT_STARTED");

        return orderRepository.save(newOrder);
    }
}
