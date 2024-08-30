package com.telerikacademy.web.smartgarageti.controllers.mvc;


import com.telerikacademy.web.smartgarageti.models.Vehicle;
import com.telerikacademy.web.smartgarageti.services.contracts.VehicleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/ti/vehicles")
public class VehicleMvcController {
    private final VehicleService vehicleService;

    public VehicleMvcController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session){
        return session.getAttribute("currentUser") !=null;
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping
    public String showVehicles(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> vehiclePage = vehicleService.getAllVehicles(pageable);

        model.addAttribute("vehicles", vehiclePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", vehiclePage.getTotalPages());
        model.addAttribute("size", size);
        return "Vehicles";
    }
}
