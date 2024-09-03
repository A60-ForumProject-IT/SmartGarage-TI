package com.telerikacademy.web.smartgarageti.controllers.mvc;

import com.telerikacademy.web.smartgarageti.exceptions.AuthenticationException;
import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.exceptions.UnauthorizedOperationException;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.models.BaseService;
import com.telerikacademy.web.smartgarageti.models.RepairService;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.RepairServiceDto;
import com.telerikacademy.web.smartgarageti.services.contracts.BaseServiceService;
import com.telerikacademy.web.smartgarageti.services.contracts.RepairServiceService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ti/services")
public class RepairServiceMvcController {
    public static final int ENGINE_DIAGNOSTICS_ID = 1;
    public static final int TIRE_WHEEL_ID = 6;
    public static final int LUBE_OIL_FILTERS_ID = 2;
    public static final int BRAKE_REPAIR_ID = 5;
    public static final int BELTS_HOSES_ID = 3;
    public static final int AIR_CONDITIONING_ID = 4;
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

    @ModelAttribute("user")
    public User populateUser(HttpSession session) {
        if (session.getAttribute("currentUser") != null) {
            return authenticationHelper.tryGetUserFromSession(session);
        }
        return null;
    }

    @GetMapping
    public String showRepairServices() {
        return "services";
    }

    @GetMapping("/air-conditioning")
    public String showAirConditioning(Model model) {
        try {
            BaseService baseService = baseServiceService.getBaseServiceById(AIR_CONDITIONING_ID);
            List<RepairService> services = repairServiceService.findAllByBaseService_IdAndIsDeletedFalse(baseService.getId());
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
            BaseService baseService = baseServiceService.getBaseServiceById(BELTS_HOSES_ID);
            List<RepairService> services = repairServiceService.findAllByBaseService_IdAndIsDeletedFalse(baseService.getId());
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
            BaseService baseService = baseServiceService.getBaseServiceById(BRAKE_REPAIR_ID);
            List<RepairService> services = repairServiceService.findAllByBaseService_IdAndIsDeletedFalse(baseService.getId());
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
            BaseService baseService = baseServiceService.getBaseServiceById(ENGINE_DIAGNOSTICS_ID);
            List<RepairService> services = repairServiceService.findAllByBaseService_IdAndIsDeletedFalse(baseService.getId());
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
            BaseService baseService = baseServiceService.getBaseServiceById(LUBE_OIL_FILTERS_ID);
            List<RepairService> services = repairServiceService.findAllByBaseService_IdAndIsDeletedFalse(baseService.getId());
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
            BaseService baseService = baseServiceService.getBaseServiceById(TIRE_WHEEL_ID);
            List<RepairService> services = repairServiceService.findAllByBaseService_IdAndIsDeletedFalse(baseService.getId());
            model.addAttribute("services", services);
            return "service_tire_wheel";
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "404";
        }
    }

    @PostMapping("/{slug}/add")
    public ResponseEntity<?> addService(@PathVariable String slug,
                                        @Valid @RequestBody RepairServiceDto serviceDto, HttpSession session, Model model) {
        Map<String, String> slugToNameMap = new HashMap<>();
        slugToNameMap.put("engine-diagnostics", "Engine Diagnostics");
        slugToNameMap.put("lube-oil-filters", "Lube, Oil and Filters");
        slugToNameMap.put("belts-hoses", "Belts and Hoses");
        slugToNameMap.put("air-conditioning", "Air Conditioning");
        slugToNameMap.put("brake-repair", "Brake Repair");
        slugToNameMap.put("tire-wheel", "Tire and Wheel Services");

        User user = null;
        try {
            user = authenticationHelper.tryGetUserFromSession(session);
            if (user == null) {
                throw new AuthenticationException("User is not authenticated");
            }
        } catch (AuthenticationException e) {
            model.addAttribute("error", e.getMessage());
        }

        String baseServiceName = slugToNameMap.get(slug);
        if (baseServiceName == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Base service not found");
        }

        BaseService baseService = baseServiceService.findByName(baseServiceName);

        RepairService service = new RepairService();
        service.setName(serviceDto.getName());
        service.setPrice(serviceDto.getPrice());
        service.setBaseService(baseService);

        model.addAttribute("slug", slug);
        repairServiceService.createService(service, user);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{slug}/delete")
    public String deleteService(
            @PathVariable String slug,
            @RequestParam int serviceId,
            HttpSession session,
            Model model) {

        try {
            User loggedInUser = authenticationHelper.tryGetUserFromSession(session);
            repairServiceService.deleteService(serviceId, loggedInUser);
        } catch (AuthenticationException e) {
            model.addAttribute("error", e.getMessage());
            return "404"; //ddd
        } catch (EntityNotFoundException e) {
            model.addAttribute("error", e.getMessage());
            return "404";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("error", e.getMessage());
            return "404"; //aaa
        }

        return "redirect:/ti/services/" + slug;
    }

    @PostMapping("/{slug}/edit")
    public ResponseEntity<?> editService(
            @PathVariable String slug,
            @RequestParam int serviceId,
            @Valid @RequestBody RepairServiceDto serviceDto,
            HttpSession session,
            Model model) {

        try {
            User loggedInUser = authenticationHelper.tryGetUserFromSession(session);
            RepairService service = repairServiceService.findServiceById(serviceId);

            if (service == null) {
                throw new EntityNotFoundException("Service not found");
            }

            String currentName = service.getName().trim().toLowerCase();
            String newName = serviceDto.getName().trim().toLowerCase();

            if (!currentName.equals(newName)) {
                boolean nameExists = repairServiceService.isServiceNameTaken(newName, service.getBaseService().getId());
                if (nameExists) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Service with name " + serviceDto.getName() + " already exists!");
                }
            }

            service.setName(serviceDto.getName());
            service.setPrice(serviceDto.getPrice());
            repairServiceService.createService(service, loggedInUser);

            return ResponseEntity.ok().build();
        } catch (AuthenticationException e) {
            model.addAttribute("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DuplicateEntityException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
