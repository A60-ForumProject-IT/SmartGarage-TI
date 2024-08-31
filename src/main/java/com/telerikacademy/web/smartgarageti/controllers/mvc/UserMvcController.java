package com.telerikacademy.web.smartgarageti.controllers.mvc;

import com.telerikacademy.web.smartgarageti.exceptions.AuthenticationException;
import com.telerikacademy.web.smartgarageti.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.helpers.PermissionHelper;
import com.telerikacademy.web.smartgarageti.models.Brand;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.services.contracts.BrandService;
import com.telerikacademy.web.smartgarageti.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/ti/users")
public class UserMvcController {

    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final BrandService brandService;

    @Autowired
    public UserMvcController(UserService userService, AuthenticationHelper authenticationHelper , BrandService brandService) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.brandService = brandService;
    }

    @ModelAttribute("allVehicleBrands")
    public List<Brand> populateAllVehicleBrands() {
        // Получаване на списъка с всички брандове на коли от VehicleService
        return brandService.findAllBrands();
    }

    @GetMapping
    public String getAllUsers(HttpSession session,
                              @RequestParam(required = false) String username,
                              @RequestParam(required = false) String email,
                              @RequestParam(required = false) String phoneNumber,
                              @RequestParam(required = false) String vehicleBrand,
                              @RequestParam(required = false) LocalDate visitDateFrom,
                              @RequestParam(required = false) LocalDate visitDateTo,
                              @RequestParam(required = false) String sortBy,
                              @RequestParam(defaultValue = "asc") String sortDirection,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {

        if (vehicleBrand != null && vehicleBrand.trim().isEmpty()) {
            vehicleBrand = null;
        }

        try {
            // Извличане на текущия потребител от сесията
            User employee = authenticationHelper.tryGetUserFromSession(session);
            // Проверка дали потребителят е служител
            PermissionHelper.isEmployee(employee, "Only employees can access this resource.");

            // Настройка на сортирането
            Sort sort = Sort.unsorted();
            if (sortBy != null && !sortBy.trim().isEmpty()) {
                sort = Sort.by("asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
            }
            Pageable pageable = PageRequest.of(page, size, sort);

            // Получаване на потребители с филтрация и пагинация
            Page<User> userPage = userService.getAllUsers(employee, username, email, phoneNumber, vehicleBrand, visitDateFrom, visitDateTo, pageable);

            // Добавяне на параметри към модела за предаване към изгледа
            model.addAttribute("users", userPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", userPage.getTotalPages());
            model.addAttribute("totalItems", userPage.getTotalElements());
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("phoneNumber", phoneNumber);
            model.addAttribute("vehicleBrand", vehicleBrand);
            model.addAttribute("visitDateFrom", visitDateFrom);
            model.addAttribute("visitDateTo", visitDateTo);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("size", size);

            return "AllUsers"; // Връщане на името на изгледа

        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
