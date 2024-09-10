package com.telerikacademy.web.smartgarageti.controllers.mvc;

import java.io.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.telerikacademy.web.smartgarageti.exceptions.AuthenticationException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.models.CarServiceLog;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.models.Order;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.services.contracts.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/ti/orders")
public class OrderMvcController {
    private final OrderService orderService;
    private final AuthenticationHelper authenticationHelper;
    private final ClientCarService clientCarService;
    private final UserService userService;
    private final CarServiceLogService carServiceLogService;
    private final CurrencyConversionService currencyConversionService;

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
    public OrderMvcController(OrderService orderService, AuthenticationHelper authenticationHelper, ClientCarService clientCarService, UserService userService, CarServiceLogService carServiceLogService, CurrencyConversionService currencyConversionService) {
        this.orderService = orderService;
        this.authenticationHelper = authenticationHelper;
        this.clientCarService = clientCarService;
        this.userService = userService;
        this.carServiceLogService = carServiceLogService;
        this.currencyConversionService = currencyConversionService;
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

    @GetMapping("/{orderId}")
    public String getOrderDetails(@PathVariable int orderId, Model model, HttpSession session) {
        User loggedInUser = authenticationHelper.tryGetUserFromSession(session);

        Order order = orderService.getOrderById(orderId, loggedInUser);

        ClientCar clientCar = clientCarService.getClientCarById(order.getClientCar().getId());

        User owner = userService.getUserById(clientCar.getOwner().getId());

        List<CarServiceLog> orderServices = carServiceLogService.findCarServicesByClientCarId(clientCar.getId(), loggedInUser, owner);

        BigDecimal totalPrice = orderServices.stream()
                .map(serviceLog -> BigDecimal.valueOf(serviceLog.getCalculatedPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("orderId", orderId);
        model.addAttribute("owner", owner);
        model.addAttribute("clientCar", clientCar);
        model.addAttribute("order", order);
        model.addAttribute("orderServices", orderServices);
        model.addAttribute("totalPrice", totalPrice);

        return "SingleOrderView";
    }

    @GetMapping("/{orderId}/download-pdf")
    public ResponseEntity<byte[]> downloadOrderPdf(@PathVariable int orderId,
                                                   @RequestParam(defaultValue = "BGN") String currency,
                                                   HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);
            Order order = orderService.getOrderById(orderId, user);
            String clientFirstName = order.getClientCar().getOwner().getFirstName();
            String clientLastName = order.getClientCar().getOwner().getLastName();
            double totalPrice = orderService.calculateOrderTotalInCurrency(order, currency);

            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String formattedTotalPrice = decimalFormat.format(totalPrice);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("Order Summary").setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Order ID: " + orderId));
            document.add(new Paragraph("Client Name: " + clientFirstName + " " + clientLastName));

            for (CarServiceLog serviceLog : order.getClientCar().getCarServices()) {
                double servicePrice = serviceLog.getCalculatedPrice();
                if (!"BGN".equalsIgnoreCase(currency)) {
                    servicePrice = currencyConversionService.convertCurrency(servicePrice, "BGN", currency);
                }
                String formattedServicePrice = decimalFormat.format(servicePrice);
                document.add(new Paragraph("Service: " + serviceLog.getService().getName() + ", Price: " + formattedServicePrice + " " + currency));
            }

            document.add(new Paragraph("Total: " + formattedTotalPrice + " " + currency));
            document.close();

            byte[] pdfBytes = out.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "order_" + orderId + ".pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
