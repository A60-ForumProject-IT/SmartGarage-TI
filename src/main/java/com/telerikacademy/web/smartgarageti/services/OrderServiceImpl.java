package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.models.CarServiceLog;
import com.telerikacademy.web.smartgarageti.models.Order;
import com.telerikacademy.web.smartgarageti.repositories.contracts.OrderRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.CurrencyConversionService;
import com.telerikacademy.web.smartgarageti.services.contracts.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CurrencyConversionService currencyConversionService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, CurrencyConversionService currencyConversionService) {
        this.orderRepository = orderRepository;
        this.currencyConversionService = currencyConversionService;
    }

    @Override
    public void updateOrderStatus(int orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));

        if (!isValidStatus(newStatus)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    @Override
    public Order getOrderById(int orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) {
        return orderRepository.findAllByClientCarOwnerId(userId);
    }

    @Override
    public double calculateOrderTotalInCurrency(Order order, String currency) {
        double totalBGN = 0.0;

        for (CarServiceLog serviceLog : order.getClientCar().getCarServices()) {
            totalBGN += serviceLog.getCalculatedPrice();
        }

        if (!"BGN".equalsIgnoreCase(currency)) {
            totalBGN = currencyConversionService.convertCurrency(totalBGN, "BGN", currency);
        }

        return totalBGN;
    }

    private boolean isValidStatus(String status) {
        return status.equals("NOT_STARTED") || status.equals("IN_PROGRESS") || status.equals("READY_FOR_PICKUP");
    }
}
