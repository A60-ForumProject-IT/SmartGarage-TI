package com.telerikacademy.web.smartgarageti.controllers.mvc;

import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.services.contracts.ClientCarService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/ti/client-cars")
public class ClientCarsMvcController {
    private final AuthenticationHelper authenticationHelper;
    private final ClientCarService clientCarService;

    @Autowired
    public ClientCarsMvcController(AuthenticationHelper authenticationHelper, ClientCarService clientCarService) {
        this.authenticationHelper = authenticationHelper;
        this.clientCarService = clientCarService;
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

    @ModelAttribute("clientCars")
    public List<ClientCar> populateClientCars() {
        return clientCarService.getAllClientCars();
    }

    @GetMapping
    public String showClientCars(Model model) {
        return "ClientCars";
    }
}
