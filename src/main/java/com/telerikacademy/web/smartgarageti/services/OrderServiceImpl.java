package com.telerikacademy.web.smartgarageti.services;

import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.NoResultsFoundException;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.models.CarServiceLog;
import com.telerikacademy.web.smartgarageti.models.Order;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.repositories.contracts.OrderRepository;
import com.telerikacademy.web.smartgarageti.services.contracts.CurrencyConversionService;
import com.telerikacademy.web.smartgarageti.services.contracts.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void updateOrderStatus(int orderId, String newStatus, User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't update order statuses!");
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));

        if (!isValidStatus(newStatus)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    @Override
    public Order getOrderById(int orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));

        PermissionHelper.isEmployeeOrSameUser(user, order.getClientCar().getOwner(),
                "You are not employee or order owner to see this order details!");

        return order;
    }

    @Override
    public List<Order> getAllOrders(User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't see all orders");
        List<Order> orders = orderRepository.findAll();

        if (orders.isEmpty()) {
            throw new NoResultsFoundException("No orders found at the moment!");
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByUserId(User orderOwner, User loggedInUser) {
        PermissionHelper.isEmployeeOrSameUser(loggedInUser, orderOwner, "You are not employee or this user to see its orders!");

        List<Order> clientOrders = orderRepository.findAllByClientCarOwnerId(orderOwner.getId());

        if (clientOrders.isEmpty()) {
            throw new NoResultsFoundException("No orders found at the moment!");
        }
        return clientOrders;
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

    @Override
    @Transactional
    public List<Order> getOrdersByClientCarIdExcludingNotStarted(int clientCarId, User user) {
        PermissionHelper.isEmployee(user, "You are not employee and can't see all orders");
        return orderRepository.findByClientCar_IdAndStatusNot(clientCarId, "NOT_STARTED");
    }

    @Override
    public double calculateOrderTotalInBgn(Order order) {
        double totalBGN = 0.0;

        for (CarServiceLog serviceLog : order.getClientCar().getCarServices()) {
            totalBGN += serviceLog.getCalculatedPrice();
        }

        return totalBGN;
    }

    private boolean isValidStatus(String status) {
        return status.equals("NOT_STARTED") || status.equals("IN_PROGRESS") || status.equals("READY_FOR_PICKUP");
    }
}
