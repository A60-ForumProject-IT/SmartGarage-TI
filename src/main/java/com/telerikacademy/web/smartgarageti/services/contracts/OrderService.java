package com.telerikacademy.web.smartgarageti.services.contracts;

import com.telerikacademy.web.smartgarageti.models.Order;

import java.util.List;

public interface OrderService {

    void updateOrderStatus(int orderId, String newStatus);

    Order getOrderById(int orderId);

    List<Order> getAllOrders();

    List<Order> getOrdersByUserId(int userId);
}
