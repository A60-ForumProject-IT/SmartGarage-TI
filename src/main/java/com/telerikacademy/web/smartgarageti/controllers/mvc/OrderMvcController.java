package com.telerikacademy.web.smartgarageti.controllers.mvc;

import com.telerikacademy.web.smartgarageti.exceptions.AuthenticationException;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.models.Order;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.services.contracts.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/ti/orders")
public class OrderMvcController {
    private final OrderService orderService;
    private final AuthenticationHelper authenticationHelper;

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("user")
    public User populateUser(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return authenticationHelper.tryGetUserFromSession(session);
        }
        return null;
    }

    @ModelAttribute("isEmployee")
    public boolean populateIsEmployee(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            User user = authenticationHelper.tryGetUserFromSession(session);
            return user.getRole().getName().equals("Employee");
        }
        return false;
    }

    @Autowired
    public OrderMvcController(OrderService orderService, AuthenticationHelper authenticationHelper) {
        this.orderService = orderService;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping
    public String showOrdersPage(
            @RequestParam(required = false) Integer orderId,
            @RequestParam(required = false) String ownerUsername,
            @RequestParam(required = false) String licensePlate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate visitedBefore,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate visitedAfter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            HttpSession session
    ) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }

        if (ownerUsername != null && ownerUsername.trim().isEmpty()) {
            ownerUsername = null;
        }
        if (licensePlate != null && licensePlate.trim().isEmpty()) {
            licensePlate = null;
        }
        if (status != null && status.trim().isEmpty()) {
            status = null;
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Order> ordersPage = orderService.findAllOrdersByCriteria(
                orderId, ownerUsername, licensePlate, status, visitedBefore, visitedAfter, pageable);

        model.addAttribute("orders", ordersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ordersPage.getTotalPages());

        model.addAttribute("orderId", orderId);
        model.addAttribute("ownerUsername", ownerUsername);
        model.addAttribute("licensePlate", licensePlate);
        model.addAttribute("status", status);
        model.addAttribute("visitedBefore", visitedBefore);
        model.addAttribute("visitedAfter", visitedAfter);

        return "Orders";
    }

    @PostMapping("/update")
    public String updateOrderStatus(
            @RequestParam("orderId") int orderId,
            @RequestParam("newStatus") String newStatus,
            HttpSession session
    ) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            orderService.updateOrderStatus(orderId, newStatus, user);
        } catch (AuthenticationException e) {
            return "redirect:/ti/auth/login";
        }

        return "redirect:/ti/orders";
    }
}
