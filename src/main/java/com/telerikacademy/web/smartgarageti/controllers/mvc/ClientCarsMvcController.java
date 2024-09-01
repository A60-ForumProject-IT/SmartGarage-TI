package com.telerikacademy.web.smartgarageti.controllers.mvc;

import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.models.Brand;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.services.contracts.BrandService;
import com.telerikacademy.web.smartgarageti.services.contracts.ClientCarService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/ti/client-cars")
public class ClientCarsMvcController {
    private final AuthenticationHelper authenticationHelper;
    private final ClientCarService clientCarService;
    private final BrandService brandService;

    @Autowired
    public ClientCarsMvcController(AuthenticationHelper authenticationHelper, ClientCarService clientCarService, BrandService brandService) {
        this.authenticationHelper = authenticationHelper;
        this.clientCarService = clientCarService;
        this.brandService = brandService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }

    @ModelAttribute("isEmployee")
    public boolean populateIsEmployee(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            User user = authenticationHelper.tryGetUserFromSession(session);
            return user.getRole().getName().equals("Employee");
        }
        return false;
    }

    @ModelAttribute("allVehicleBrands")
    public List<Brand> populateAllVehicleBrands() {
        return brandService.findAllBrands();
    }

    @GetMapping
    public String listClientCars(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam(value = "sortBy", defaultValue = "") String sortBy,
            @RequestParam(value = "order", defaultValue = "asc") String order,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(order), sortBy.isEmpty() ? "owner.username" : sortBy);

        Page<ClientCar> clientCarPage;

        if (searchTerm == null || searchTerm.isEmpty()) {
            clientCarPage = clientCarService.getAllClientCars(pageable);
        } else {
            clientCarPage = clientCarService.filterAndSortClientCarsByOwner(searchTerm, pageable);
        }

        model.addAttribute("clientCars", clientCarPage.getContent());
        model.addAttribute("totalPages", clientCarPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("order", order);

        return "ClientCars";
    }
}
