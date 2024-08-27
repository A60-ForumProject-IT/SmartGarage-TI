package com.telerikacademy.web.smartgarageti.controllers.rest;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.telerikacademy.web.smartgarageti.exceptions.AuthenticationException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.NoResultsFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.models.CarServiceLog;
import com.telerikacademy.web.smartgarageti.models.Order;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.services.contracts.CurrencyConversionService;
import com.telerikacademy.web.smartgarageti.services.contracts.OrderService;
import com.telerikacademy.web.smartgarageti.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderRestController {
    private final OrderService orderService;
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final CurrencyConversionService currencyConversionService;

    @Autowired
    public OrderRestController(OrderService orderService, UserService userService, AuthenticationHelper authenticationHelper, CurrencyConversionService currencyConversionService) {
        this.orderService = orderService;
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.currencyConversionService = currencyConversionService;
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders(@RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            List<Order> orders = orderService.getAllOrders(loggedInUser);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (NoResultsFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable int orderId, @RequestParam String newStatus,
                                                  @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            orderService.updateOrderStatus(orderId, newStatus, loggedInUser);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<List<Order>> getAllOrdersForUser(@PathVariable int userId, @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            User orderOwner = userService.getUserById(loggedInUser, userId);

            List<Order> orders = orderService.getOrdersByUserId(orderOwner, loggedInUser);
            return ResponseEntity.ok(orders);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (NoResultsFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderDetails(@PathVariable int orderId, @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            Order order = orderService.getOrderById(orderId, loggedInUser);
            return ResponseEntity.ok(order);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/orders/{orderId}/total-price")
    public ResponseEntity<Double> getOrderTotalPrice(@PathVariable int orderId,
                                                     @RequestParam(required = false, defaultValue = "BGN") String currency,
                                                     @RequestHeader HttpHeaders headerss) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headerss);
            Order order = orderService.getOrderById(orderId, loggedInUser);
            double totalPrice = orderService.calculateOrderTotalInCurrency(order, currency);
            return ResponseEntity.ok(totalPrice);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/orders/{orderId}/download-pdf")
    @ResponseBody
    public ResponseEntity<byte[]> downloadOrderAsPdf(@PathVariable int orderId,
                                                     @RequestParam(defaultValue = "BGN") String currency,
                                                     @RequestHeader HttpHeaders headers) {
        try {
            User loggedInUser = authenticationHelper.tryGetUser(headers);
            Order order = orderService.getOrderById(orderId, loggedInUser);
            String clientFirstName = order.getClientCar().getOwner().getFirstName();
            String clientLastName = order.getClientCar().getOwner().getLastName();
            double totalPrice = orderService.calculateOrderTotalInCurrency(order, currency);

            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String formattedTotalPrice = decimalFormat.format(totalPrice);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("Order Summary")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Order ID: " + orderId));
            document.add(new Paragraph("Client Name: " + clientFirstName + " " + clientLastName));

            for (CarServiceLog serviceLog : order.getClientCar().getCarServices()) {
                double servicePrice = serviceLog.getCalculatedPrice();
                if (!"BGN".equalsIgnoreCase(currency)) {
                    servicePrice = currencyConversionService.convertCurrency(servicePrice, "BGN", currency);
                }
                String formattedServicePrice = decimalFormat.format(servicePrice);
                document.add(new Paragraph("Service: " + serviceLog.getService().getName() +
                        ", Price: " + formattedServicePrice + " " + currency));
            }

            document.add(new Paragraph("Total: " + formattedTotalPrice + " " + currency));
            document.close();

            byte[] pdfBytes = out.toByteArray();

            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "order_" + orderId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
