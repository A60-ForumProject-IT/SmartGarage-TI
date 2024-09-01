package com.telerikacademy.web.smartgarageti.controllers.mvc;

import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.models.BaseService;
import com.telerikacademy.web.smartgarageti.models.RepairService;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.services.contracts.BaseServiceService;
import com.telerikacademy.web.smartgarageti.services.contracts.RepairServiceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/ti/services")
public class RepairServiceMvcController {
    private final AuthenticationHelper authenticationHelper;
    private final BaseServiceService baseServiceService;
    private final RepairServiceService repairServiceService;

    @Autowired
    public RepairServiceMvcController(AuthenticationHelper authenticationHelper, BaseServiceService baseServiceService, RepairServiceService repairServiceService) {
        this.authenticationHelper = authenticationHelper;
        this.baseServiceService = baseServiceService;
        this.repairServiceService = repairServiceService;
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
    public String showAirConditioning(Model model) {
        try {
            BaseService baseService = baseServiceService.getBaseServiceById(4);
            List<RepairService> services = repairServiceService.getAllByBaseServiceId(baseService.getId());
            model.addAttribute("services", services);
            return "service_air_conditioning";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "404";
        }
    }

    @GetMapping("/belts-hoses")
    public String showBeltsAndHoses(Model model) {
        try {
            BaseService baseService = baseServiceService.getBaseServiceById(3);
            List<RepairService> services = repairServiceService.getAllByBaseServiceId(baseService.getId());
            model.addAttribute("services", services);
            return "service_belts_hoses";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "404";
        }
    }

    @GetMapping("/brake-repair")
    public String showBrakeRepair(Model model) {
        try {
            BaseService baseService = baseServiceService.getBaseServiceById(5);
            List<RepairService> services = repairServiceService.getAllByBaseServiceId(baseService.getId());
            model.addAttribute("services", services);
            return "service_brake_repair";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "404";
        }
    }

    @GetMapping("/engine-diagnostics")
    public String showEngineDiagnostics(Model model) {
        try {
            BaseService baseService = baseServiceService.getBaseServiceById(1);
            List<RepairService> services = repairServiceService.getAllByBaseServiceId(baseService.getId());
            model.addAttribute("services", services);
            return "service_engine_diagnostics";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "404";
        }
    }

    @GetMapping("/lube-oil-filters")
    public String showLubeOilFilters(Model model) {
        try {
            BaseService baseService = baseServiceService.getBaseServiceById(2);
            List<RepairService> services = repairServiceService.getAllByBaseServiceId(baseService.getId());
            model.addAttribute("services", services);
            return "service_lube_oil_filters";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "404";
        }
    }

    @GetMapping("/tire-wheel")
    public String showTireWheel(Model model) {
        try {
            BaseService baseService = baseServiceService.getBaseServiceById(6);
            List<RepairService> services = repairServiceService.getAllByBaseServiceId(baseService.getId());
            model.addAttribute("services", services);
            return "service_tire_wheel";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "404";
        }
    }
}
