package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.NoResultsFoundException;
import com.telerikacademy.web.smartgarageti.models.CarServiceLog;
import com.telerikacademy.web.smartgarageti.models.Order;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.repositories.contracts.OrderRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.CurrencyConversionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.telerikacademy.web.smartgarageti.helpers.TestHelpers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CurrencyConversionService currencyConversionService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void updateOrderStatus_ShouldUpdateStatus_WhenOrderExistsAndStatusIsValid() {
        int orderId = 1;
        String newStatus = "IN_PROGRESS";
        User mockUser = createMockUserEmployee();
        Order mockOrder = createMockOrder();
        mockOrder.setStatus("NOT_STARTED");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        orderService.updateOrderStatus(orderId, newStatus, mockUser);

        assertEquals(newStatus, mockOrder.getStatus());
        verify(orderRepository).save(mockOrder);
    }

    @Test
    void updateOrderStatus_ShouldThrowIllegalArgumentException_WhenStatusIsInvalid() {
        int orderId = 1;
        String invalidStatus = "INVALID_STATUS";
        User mockUser = createMockUserEmployee();
        Order mockOrder = createMockOrder();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        assertThrows(IllegalArgumentException.class, () ->
                orderService.updateOrderStatus(orderId, invalidStatus, mockUser));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenOrderExists() {
        int orderId = 1;
        User mockUser = createMockUserEmployee();
        Order mockOrder = createMockOrder();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));

        Order result = orderService.getOrderById(orderId, mockUser);

        assertEquals(mockOrder, result);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrderById_ShouldThrowEntityNotFoundException_WhenOrderDoesNotExist() {
        int orderId = 1;
        User mockUser = createMockUserEmployee();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                orderService.getOrderById(orderId, mockUser));

        verify(orderRepository).findById(orderId);
    }

    @Test
    void getAllOrders_ShouldReturnOrders_WhenOrdersExist() {
        User mockUser = createMockUserEmployee();
        List<Order> mockOrders = List.of(createMockOrder());

        when(orderRepository.findAll()).thenReturn(mockOrders);

        List<Order> result = orderService.getAllOrders(mockUser);

        assertEquals(mockOrders, result);
        verify(orderRepository).findAll();
    }

    @Test
    void getAllOrders_ShouldThrowNoResultsFoundException_WhenNoOrdersExist() {
        User mockUser = createMockUserEmployee();

        when(orderRepository.findAll()).thenReturn(List.of());

        assertThrows(NoResultsFoundException.class, () ->
                orderService.getAllOrders(mockUser));

        verify(orderRepository).findAll();
    }

    @Test
    void getOrdersByUserId_ShouldReturnOrders_WhenOrdersExist() {
        User mockUser = createMockUserEmployee();
        User mockOrderOwner = createMockUser();
        List<Order> mockOrders = List.of(createMockOrder());

        when(orderRepository.findAllByClientCarOwnerId(mockOrderOwner.getId())).thenReturn(mockOrders);

        List<Order> result = orderService.getOrdersByUserId(mockOrderOwner, mockUser);

        assertEquals(mockOrders, result);
        verify(orderRepository).findAllByClientCarOwnerId(mockOrderOwner.getId());
    }

    @Test
    void getOrdersByUserId_ShouldThrowNoResultsFoundException_WhenNoOrdersExist() {
        User mockUser = createMockUserEmployee();
        User mockOrderOwner = createMockUser();

        when(orderRepository.findAllByClientCarOwnerId(mockOrderOwner.getId())).thenReturn(List.of());

        assertThrows(NoResultsFoundException.class, () ->
                orderService.getOrdersByUserId(mockOrderOwner, mockUser));

        verify(orderRepository).findAllByClientCarOwnerId(mockOrderOwner.getId());
    }

    @Test
    void calculateOrderTotalInCurrency_ShouldReturnTotalInSpecifiedCurrency() {
        Order mockOrder = createMockOrder();
        String targetCurrency = "USD";
        double mockConvertedTotal = 120.0;

        when(currencyConversionService.convertCurrency(anyDouble(), eq("BGN"), eq(targetCurrency)))
                .thenReturn(mockConvertedTotal);

        double result = orderService.calculateOrderTotalInCurrency(mockOrder, targetCurrency);

        assertEquals(mockConvertedTotal, result);
        verify(currencyConversionService).convertCurrency(anyDouble(), eq("BGN"), eq(targetCurrency));
    }

    @Test
    void calculateOrderTotalInCurrency_ShouldNotConvertCurrency_WhenCurrencyIsBGN() {
        Order mockOrder = createMockOrder();
        String targetCurrency = "BGN";
        double totalBGN = 0.0;

        for (CarServiceLog serviceLog : mockOrder.getClientCar().getCarServices()) {
            totalBGN += serviceLog.getCalculatedPrice();
        }

        double result = orderService.calculateOrderTotalInCurrency(mockOrder, targetCurrency);

        assertEquals(totalBGN, result);
        verify(currencyConversionService, never()).convertCurrency(anyDouble(), anyString(), anyString());
    }

    @Test
    void calculateOrderTotalInCurrency_ShouldConvertCurrency_WhenCurrencyIsNotBGN() {
        Order mockOrder = createMockOrder();
        String targetCurrency = "USD";
        double mockConvertedTotal = 120.0;

        mockOrder.getClientCar().getCarServices().clear();
        CarServiceLog log1 = new CarServiceLog();
        log1.setCalculatedPrice(50.0);
        CarServiceLog log2 = new CarServiceLog();
        log2.setCalculatedPrice(50.0);

        mockOrder.getClientCar().getCarServices().add(log1);
        mockOrder.getClientCar().getCarServices().add(log2);

        when(currencyConversionService.convertCurrency(eq(100.0), eq("BGN"), eq(targetCurrency)))
                .thenReturn(mockConvertedTotal);

        double result = orderService.calculateOrderTotalInCurrency(mockOrder, targetCurrency);

        assertEquals(mockConvertedTotal, result);
        verify(currencyConversionService).convertCurrency(eq(100.0), eq("BGN"), eq(targetCurrency));
    }

}
