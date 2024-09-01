package com.telerikacademy.web.smartgarageti.controllers.mvc;

import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.models.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ti/services")
public class RepairServiceMvcController {
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public RepairServiceMvcController(AuthenticationHelper authenticationHelper) {
        this.authenticationHelper = authenticationHelper;
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

    @GetMapping
    public String showRepairServices() {
        return "services";
    }

    @GetMapping("/air-conditioning")
    public String showAirConditioning() {
        return "service_air_conditioning";
    }

    @GetMapping("/belts-hoses")
    public String showBeltsAndHoses() {
        return "service_belts_hoses";
    }

    @GetMapping("/brake-repair")
    public String showBrakeRepair() {
        return "service_brake_repair";
    }

    @GetMapping("/engine-diagnostics")
    public String showEngineDiagnostics() {
        return "service_engine_diagnostics";
    }

    @GetMapping("/lube-oil-filters")
    public String showLubeOilFilters() {
        return "service_lube_oil_filters";
    }

    @GetMapping("/tire-wheel")
    public String showTireWheel() {
        return "service_tire_wheel";
    }
}
