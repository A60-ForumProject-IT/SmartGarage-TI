package com.telerikacademy.web.smartgarageti.controllers.mvc;

import java.io.ByteArrayOutputStream;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
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

        List<CarServiceLog> orderServices = carServiceLogService.findOrderServicesByOrderId(order.getId(), loggedInUser, owner);

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
    public ResponseEntity<byte[]> downloadStyledInvoicePdf(@PathVariable int orderId,
                                                           @RequestParam(defaultValue = "BGN") String currency,
                                                           HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUserFromSession(session);

            Order order = orderService.getOrderById(orderId, user);

            User owner = order.getClientCar().getOwner();

            List<CarServiceLog> orderServices = carServiceLogService.findOrderServicesByOrderId(order.getId(), user, owner);

            byte[] pdfBytes = generateStyledInvoice(order, orderServices, currency);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice_" + orderId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    public byte[] generateStyledInvoice(Order order, List<CarServiceLog> orderServices, String currency) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument, PageSize.A4);
            document.setMargins(20, 20, 20, 20);

            Color blueColor = new DeviceRgb(0, 102, 204);
            Color greyColor = new DeviceRgb(245, 245, 245);
            Color darkGreyColor = new DeviceRgb(180, 180, 180);
            Color whiteColor = new DeviceRgb(255, 255, 255);

            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{15, 85}))
                    .useAllAvailableWidth()
                    .setBorder(Border.NO_BORDER);

            String logoPath = "src/main/resources/static/images/SmartGarageLogo.jpg"; // Примерен път до логото
            Image logo = new Image(ImageDataFactory.create(logoPath)).scaleToFit(120, 120);  // Увеличаваме логото
            Cell logoCell = new Cell().add(logo)
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.LEFT);
            headerTable.addCell(logoCell);

            Paragraph invoiceTitle = new Paragraph("Service Invoice")
                    .setFontSize(20)
                    .setBold()
                    .setFontColor(blueColor)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setUnderline();
            Cell titleCell = new Cell().add(invoiceTitle)
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT);
            headerTable.addCell(titleCell);

            document.add(headerTable);

            Paragraph invoiceDetails = new Paragraph("Date: " + order.getOrderDate() + "\nInvoice #" + order.getId())
                    .setTextAlignment(TextAlignment.RIGHT).setUnderline();
            document.add(invoiceDetails);

            document.add(new Paragraph("\n").setBackgroundColor(darkGreyColor).setHeight(2));

            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .useAllAvailableWidth()
                    .setBorder(Border.NO_BORDER);

            Cell invoiceToCell = new Cell().setBorder(Border.NO_BORDER);

            Paragraph invoiceTo = new Paragraph("Invoice To:")
                    .setFontSize(12)
                    .setBold()
                    .setFontColor(blueColor);
            invoiceToCell.add(invoiceTo);

            Paragraph ownerLabel = new Paragraph("Owner:")
                    .setBold()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.LEFT);

            Paragraph ownerInfo = new Paragraph(order.getClientCar().getOwner().getFirstName() + " " + order.getClientCar().getOwner().getLastName())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.LEFT);

            Paragraph phoneLabel = new Paragraph("Phone Number:")
                    .setBold()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.LEFT);

            Paragraph phoneInfo = new Paragraph(order.getClientCar().getOwner().getPhoneNumber())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.LEFT);

            Paragraph emailLabel = new Paragraph("E-mail:")
                    .setBold()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.LEFT);

            Paragraph emailInfo = new Paragraph(order.getClientCar().getOwner().getEmail())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.LEFT);

            invoiceToCell.add(ownerLabel);
            invoiceToCell.add(ownerInfo);
            invoiceToCell.add(new Paragraph("\n"));
            invoiceToCell.add(phoneLabel);
            invoiceToCell.add(phoneInfo);
            invoiceToCell.add(new Paragraph("\n"));
            invoiceToCell.add(emailLabel);
            invoiceToCell.add(emailInfo);

            infoTable.addCell(invoiceToCell);

            Cell carInfoCell = new Cell().setBorder(Border.NO_BORDER);

            Paragraph carInfo = new Paragraph("Car Info:")
                    .setFontSize(12)
                    .setBold()
                    .setFontColor(blueColor)
                    .setTextAlignment(TextAlignment.RIGHT);
            carInfoCell.add(carInfo);

            Paragraph vinLabel = new Paragraph("Vin:")
                    .setBold()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);

            Paragraph vinInfo = new Paragraph(order.getClientCar().getVin())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);

            Paragraph licensePlateLabel = new Paragraph("License Plate:")
                    .setBold()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);

            Paragraph licensePlateInfo = new Paragraph(order.getClientCar().getLicensePlate())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);

            Paragraph brandLabel = new Paragraph("Brand:")
                    .setBold()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);

            Paragraph brandInfo = new Paragraph(order.getClientCar().getVehicle().getBrand().getName())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);

            Paragraph modelLabel = new Paragraph("Model:")
                    .setBold()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);

            Paragraph modelInfo = new Paragraph(order.getClientCar().getVehicle().getModel().getName())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);

            Paragraph engineTypeLabel = new Paragraph("Engine Type:")
                    .setBold()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);

            Paragraph engineTypeInfo = new Paragraph(order.getClientCar().getVehicle().getEngineType().getName())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);

            Paragraph yearLabel = new Paragraph("Year:")
                    .setBold()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);

            Paragraph yearInfo = new Paragraph(String.valueOf(order.getClientCar().getVehicle().getYear().getYear()))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);

            carInfoCell.add(vinLabel);
            carInfoCell.add(vinInfo);
            carInfoCell.add(licensePlateLabel);
            carInfoCell.add(licensePlateInfo);
            carInfoCell.add(brandLabel);
            carInfoCell.add(brandInfo);
            carInfoCell.add(modelLabel);
            carInfoCell.add(modelInfo);
            carInfoCell.add(engineTypeLabel);
            carInfoCell.add(engineTypeInfo);
            carInfoCell.add(yearLabel);
            carInfoCell.add(yearInfo);

            infoTable.addCell(carInfoCell);

            document.add(infoTable);

            document.add(new Paragraph("\n").setBackgroundColor(darkGreyColor).setHeight(2));

            Table table = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
                    .useAllAvailableWidth()
                    .setBorder(Border.NO_BORDER);

            Cell descriptionHeader = new Cell().add(new Paragraph("Description")
                            .setFontColor(whiteColor)
                            .setBold()
                            .setFontSize(12))
                    .setBackgroundColor(blueColor)
                    .setBorderBottom(new SolidBorder(darkGreyColor, 2))
                    .setBorderTop(new SolidBorder(darkGreyColor, 2))
                    .setBorderLeft(Border.NO_BORDER)
                    .setBorderRight(Border.NO_BORDER);
            table.addCell(descriptionHeader);

            Cell amountHeader = new Cell().add(new Paragraph("Amount")
                            .setFontColor(whiteColor)
                            .setBold()
                            .setFontSize(12))
                    .setBackgroundColor(blueColor)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorderBottom(new SolidBorder(darkGreyColor, 2))
                    .setBorderTop(new SolidBorder(darkGreyColor, 2))
                    .setBorderLeft(Border.NO_BORDER)
                    .setBorderRight(Border.NO_BORDER);
            table.addCell(amountHeader);

            DecimalFormat decimalFormat = new DecimalFormat("0.00");

            for (CarServiceLog serviceLog : orderServices) {
                double servicePrice = serviceLog.getCalculatedPrice();
                if (!"BGN".equalsIgnoreCase(currency)) {
                    servicePrice = currencyConversionService.convertCurrency(servicePrice, "BGN", currency);
                }
                String formattedServicePrice = decimalFormat.format(servicePrice);

                Cell serviceCell = new Cell().add(new Paragraph(serviceLog.getService().getName())
                                .setFontSize(10))
                        .setBorderBottom(new SolidBorder(greyColor, 0.5f))
                        .setBorderTop(Border.NO_BORDER)
                        .setBorderLeft(Border.NO_BORDER)
                        .setBorderRight(Border.NO_BORDER);
                table.addCell(serviceCell);

                Cell priceCell = new Cell().add(new Paragraph(formattedServicePrice + " " + currency)
                                .setFontSize(10))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setBorderBottom(new SolidBorder(greyColor, 0.5f))
                        .setBorderTop(Border.NO_BORDER)
                        .setBorderLeft(Border.NO_BORDER)
                        .setBorderRight(Border.NO_BORDER);
                table.addCell(priceCell);
            }

            document.add(table);

            document.add(new Paragraph("\n"));
            Paragraph total = new Paragraph("Total: " + decimalFormat.format(orderService.calculateOrderTotalInCurrency(order, currency)) + " " + currency)
                    .setFontSize(12)
                    .setBold()
                    .setFontColor(blueColor)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorderTop(new SolidBorder(greyColor, 1));
            document.add(total);

            document.add(new Paragraph("\n"));
            Paragraph footer = new Paragraph("If you have any questions about this invoice, please contact\n"
                    + "Phone: (+359) 888 667788\nE-mail: ikaragyozov19@gmail.com")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(darkGreyColor);
            document.add(footer);

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
