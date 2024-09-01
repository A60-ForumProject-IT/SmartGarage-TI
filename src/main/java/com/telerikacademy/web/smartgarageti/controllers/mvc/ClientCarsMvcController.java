package com.telerikacademy.web.smartgarageti.controllers.mvc;

import com.telerikacademy.web.smartgarageti.exceptions.DuplicateEntityException;
import com.telerikacademy.web.smartgarageti.exceptions.EntityNotFoundException;
import com.telerikacademy.web.smartgarageti.helpers.AuthenticationHelper;
import com.telerikacademy.web.smartgarageti.helpers.MapperHelper;
import com.telerikacademy.web.smartgarageti.models.Brand;
import com.telerikacademy.web.smartgarageti.models.ClientCar;
import com.telerikacademy.web.smartgarageti.models.User;
import com.telerikacademy.web.smartgarageti.models.dto.ClientCarDtoMvc;
import com.telerikacademy.web.smartgarageti.services.contracts.BrandService;
import com.telerikacademy.web.smartgarageti.services.contracts.ClientCarService;
import com.telerikacademy.web.smartgarageti.services.contracts.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/ti/client-cars")
public class ClientCarsMvcController {
    private final AuthenticationHelper authenticationHelper;
    private final ClientCarService clientCarService;
    private final BrandService brandService;
    private final UserService userService;
    private final MapperHelper mapperHelper;

    @Autowired
    public ClientCarsMvcController(AuthenticationHelper authenticationHelper, ClientCarService clientCarService, BrandService brandService, UserService userService, MapperHelper mapperHelper) {
        this.authenticationHelper = authenticationHelper;
        this.clientCarService = clientCarService;
        this.brandService = brandService;
        this.userService = userService;
        this.mapperHelper = mapperHelper;
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

    @GetMapping("/usernames")
    @ResponseBody
    public List<String> findUsernames(@RequestParam("term") String term) {
        List<String> usernames = userService.findUsernamesByTerm(term);
        System.out.println("Usernames found: " + usernames);
        return usernames;
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
        model.addAttribute("clientCarDtoMvc", new ClientCarDtoMvc());

        return "ClientCars";
    }

    @PostMapping
    public String addClientCar(@Valid @ModelAttribute ClientCarDtoMvc clientCarDtoMvc,
                               Model model,
                               HttpSession session) {

        try {
            User loggedInUser = authenticationHelper.tryGetUserFromSession(session);
            User userToAddCar = userService.getByUsername(clientCarDtoMvc.getOwner());
            Brand brand = brandService.findBrandByName(clientCarDtoMvc.getBrandName());

            ClientCar newClientCar = mapperHelper.createClientCarFromDto(clientCarDtoMvc, userToAddCar, brand);

            clientCarService.createClientCar(newClientCar, loggedInUser);
            return "redirect:/ti/client-cars";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "404";
        } catch (DuplicateEntityException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "404";
        }
    }
}
