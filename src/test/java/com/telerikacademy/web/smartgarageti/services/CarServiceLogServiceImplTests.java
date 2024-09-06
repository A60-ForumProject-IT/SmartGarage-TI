package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.NoResultsFoundException;
import com.telerikacademy.web.smartgarageti.models.*;
import com.telerikacademy.web.smartgarageti.repositories.contracts.CarServiceLogRepository;
import com.telerikacademy.web.smartgarageti.repositories.contracts.ClientCarRepository;
import com.telerikacademy.web.smartgarageti.repositories.contracts.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.telerikacademy.web.smartgarageti.helpers.TestHelpers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceLogServiceImplTests {

    @Mock
    private CarServiceLogRepository carServiceLogRepository;

    @Mock
    private ClientCarRepository clientCarRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientCarServiceImpl clientCarService;

    @InjectMocks
    private CarServiceLogServiceImpl carServiceLogService;

    @Test
    void findAllCarsServiceLogs_ShouldReturnServiceLogs_WhenLogsExist() {
        List<CarServiceLog> mockLogs = List.of(new CarServiceLog(), new CarServiceLog());

        when(carServiceLogRepository.findAll()).thenReturn(mockLogs);

        List<CarServiceLog> result = carServiceLogService.findAllCarsServiceLogs(createMockUserEmployee());

        assertEquals(mockLogs, result);
        verify(carServiceLogRepository).findAll();
    }

    @Test
    void findAllCarsServiceLogs_ShouldThrowNoResultsFoundException_WhenNoLogsExist() {
        when(carServiceLogRepository.findAll()).thenReturn(List.of());

        assertThrows(NoResultsFoundException.class, () ->
                carServiceLogService.findAllCarsServiceLogs(createMockUserEmployee()));

        verify(carServiceLogRepository).findAll();
    }

    @Test
    void findCarServicesByClientCarId_ShouldReturnServiceLogs_WhenLogsExist() {
        int clientCarId = 1;
        List<CarServiceLog> mockLogs = List.of(new CarServiceLog(), new CarServiceLog());

        when(carServiceLogRepository.findAllByClientCarId(clientCarId)).thenReturn(mockLogs);

        List<CarServiceLog> result = carServiceLogService.findCarServicesByClientCarId(clientCarId, createMockUserEmployee(), createMockUser());

        assertEquals(mockLogs, result);
        verify(carServiceLogRepository).findAllByClientCarId(clientCarId);
    }

    @Test
    void findCarServicesByClientCarId_ShouldThrowNoResultsFoundException_WhenNoLogsExist() {
        int clientCarId = 1;

        when(carServiceLogRepository.findAllByClientCarId(clientCarId)).thenReturn(List.of());

        assertThrows(NoResultsFoundException.class, () ->
                carServiceLogService.findCarServicesByClientCarId(clientCarId, createMockUserEmployee(), createMockUser()));

        verify(carServiceLogRepository).findAllByClientCarId(clientCarId);
    }

    @Test
    void addServiceToOrder_ShouldAddServiceLogToExistingOrder_WhenActiveOrderExists() {
        int clientCarId = 1;
        RepairService mockService = createMockRepairService();
        Order mockOrder = createMockOrder();
        ClientCar mockClientCar = createMockClientCar();
        List<CarServiceLog> serviceLogs = mockClientCar.getCarServices();
        CarServiceLog carServiceLog = createMockCarServiceLog();
        serviceLogs.add(carServiceLog);
        mockClientCar.setId(clientCarId);

        when(orderRepository.findActiveOrderByClientCarId(clientCarId)).thenReturn(Optional.of(mockOrder));
        when(clientCarService.getClientCarById(clientCarId)).thenReturn(mockClientCar);
        when(carServiceLogRepository.save(any(CarServiceLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CarServiceLog result = carServiceLogService.addServiceToOrder(clientCarId, mockService, createMockUserEmployee());

        assertEquals(mockClientCar, result.getClientCar());
        assertEquals(mockService, result.getService());
        assertEquals(mockOrder, result.getOrder());
        verify(carServiceLogRepository).save(any(CarServiceLog.class));
        verify(clientCarRepository).save(mockClientCar);
    }

    @Test
    void getServiceHistoryForClientCars_ShouldReturnServiceLogs_WhenLogsExist() {
        User mockLoggedInUser = createMockUserEmployee();
        User mockCarOwner = createMockUser();
        List<ClientCar> mockClientCars = List.of(createMockClientCar());
        List<CarServiceLog> mockServiceLogs = List.of(createMockCarServiceLog());

        when(carServiceLogRepository.findAllByClientCarIn(mockClientCars)).thenReturn(mockServiceLogs);

        List<CarServiceLog> result = carServiceLogService.getServiceHistoryForClientCars(mockClientCars, mockLoggedInUser, mockCarOwner);

        assertEquals(mockServiceLogs, result);
        verify(carServiceLogRepository).findAllByClientCarIn(mockClientCars);
    }

    @Test
    void getServiceHistoryForClientCars_ShouldThrowNoResultsFoundException_WhenNoLogsExist() {
        User mockLoggedInUser = createMockUserEmployee();
        User mockCarOwner = createMockUser();
        List<ClientCar> mockClientCars = List.of(createMockClientCar());

        when(carServiceLogRepository.findAllByClientCarIn(mockClientCars)).thenReturn(List.of());

        assertThrows(NoResultsFoundException.class, () ->
                carServiceLogService.getServiceHistoryForClientCars(mockClientCars, mockLoggedInUser, mockCarOwner));

        verify(carServiceLogRepository).findAllByClientCarIn(mockClientCars);
    }

    @Test
    void findAllServicesByOwnerId_ShouldReturnServiceLogs_WhenLogsExist() {
        int ownerId = 1;
        List<CarServiceLog> mockServiceLogs = List.of(createMockCarServiceLog());

        when(carServiceLogRepository.findAllByClientCarOwnerId(ownerId)).thenReturn(mockServiceLogs);

        List<CarServiceLog> result = carServiceLogService.findAllServicesByOwnerId(ownerId);

        assertEquals(mockServiceLogs, result);
        verify(carServiceLogRepository).findAllByClientCarOwnerId(ownerId);
    }

    @Test
    void addServiceToOrder_ShouldCreateNewOrderAndAddServiceLog_WhenNoActiveOrderExists() {
        int clientCarId = 1;
        RepairService mockService = createMockRepairService();
        ClientCar mockClientCar = createMockClientCar();
        Order newOrder = createMockOrder();
        newOrder.setClientCar(mockClientCar);
        newOrder.setStatus("NOT_STARTED");

        when(orderRepository.findActiveOrderByClientCarId(clientCarId)).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(newOrder);
        when(clientCarService.getClientCarById(clientCarId)).thenReturn(mockClientCar);
        when(carServiceLogRepository.save(any(CarServiceLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CarServiceLog result = carServiceLogService.addServiceToOrder(clientCarId, mockService, createMockUserEmployee());

        assertEquals(mockClientCar, result.getClientCar());
        assertEquals(mockService, result.getService());
        assertEquals(newOrder, result.getOrder());
        verify(orderRepository).save(any(Order.class));
        verify(carServiceLogRepository).save(any(CarServiceLog.class));
    }
}
